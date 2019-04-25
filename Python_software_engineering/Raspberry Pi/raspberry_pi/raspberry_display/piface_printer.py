from threading import Timer
from time import sleep
import pifacecad


LCD_ROW_LEN = 16


class PIFacePrinter:
    # refresh_speed_sec -- time to clean the screen
    def __init__(self, refresh_speed_sec, cad):
        self._cad = cad
        self._cad.lcd.backlight_on()
        self._timer = None
        self._refresh_time = refresh_speed_sec
        self._permanent_text = ""
        self._temporary_text = ""
        self._last_button_one = None
        self._ticks_left = 0

    def _shift_right(self):
        if self._ticks_left == 0:
            self._write_permanent_text()
            return
        self._cad.lcd.move_left()
        self._ticks_left = self._ticks_left - 1
        self._timer = Timer(self._refresh_time, self._shift_right)
        self._timer.start()

    def write_text(self, text):
        self._clean_timer()
        self._cad.lcd.clear()
        self._cad.lcd.write(text)

        exceeding_text_size = len(text) - LCD_ROW_LEN
        if exceeding_text_size > 0:
            self._ticks_left = exceeding_text_size
            self._shift_right()
        else:
            sleep(self._refresh_time * 4)
            self._write_permanent_text()

    def write_permanent(self, text):
        self._permanent_text = text
        self._write_permanent_text()

    def _write_permanent_text(self):
        self._cad.lcd.clear()
        self._cad.lcd.write(self._permanent_text)
        self._cad.lcd.left_justify()

    def _clean_timer(self):
        if self._timer is not None:
            self._timer.cancel()
            self._timer = None
            self._write_permanent_text()

    def _press_one(self, event):
        self._last_button_one = True
        print("one pressed")

    def _press_two(self, event):
        self._last_button_one = False
        print("two pressed")

    def wait_button_press(self):
        self._last_button_one = None
        listener = pifacecad.SwitchEventListener(chip=self._cad)
        listener.register(0, pifacecad.IODIR_FALLING_EDGE, self._press_one)
        listener.register(1, pifacecad.IODIR_FALLING_EDGE, self._press_two)
        listener.activate()
        while self._last_button_one is None:
            sleep(0.5)
        listener.deregister(0, pifacecad.IODIR_FALLING_EDGE)
        listener.deregister(1, pifacecad.IODIR_FALLING_EDGE)
        listener.deactivate()
        return self._last_button_one

    def free(self):
        self._cad.lcd.backlight_off()
        self._clean_timer()

    def __del__(self):
        self.free()
