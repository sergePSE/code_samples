from threading import Timer

LCD_ROW_LEN = 16


class TerminalPrinter:
    # refresh_speed_sec -- time to clean the screen
    def __init__(self, refresh_speed_sec):
        self._timer = None
        self._refresh_time = refresh_speed_sec
        self._permanent_text = ""

    @staticmethod
    def wait_button_press():
        print("enter yes or no")
        answer = input()
        while answer != "yes" and answer != "no":
            answer = input()
        return answer == "yes"

    def write_text(self, text):
        print(text)
        self._clean_timer()
        self._timer = Timer(self._refresh_time, self._write_permanent_text)
        self._timer.start()

    def write_permanent(self, text):
        self._permanent_text = text
        self._write_permanent_text()

    def _write_permanent_text(self):
        print(self._permanent_text)

    def _clean_timer(self):
        if self._timer is not None:
            self._timer.cancel()
            self._timer = None

    def free(self):
        self._clean_timer()

    def __del__(self):
        self.free()
