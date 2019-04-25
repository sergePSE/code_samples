import signal
import pifacecad
import settings
from qr_recognition.recogniser import QRCodeRecogniser
from qr_recognition.timed_buffer import TimedBuffer
from raspberry_display.piface_printer import PIFacePrinter


is_end = False


# test cut version for the raspberry PI without web server involvement
def main():
    print("Press ctrl-C to exit")
    cad = pifacecad.PiFaceCAD()
    display = PIFacePrinter(refresh_speed_sec=settings.display_refresh_time_sec, cad=cad)
    recogniser = QRCodeRecogniser(TimedBuffer(settings.code_repetition_time_sec))
    display.write_permanent("Waiting for qr code")
    while not is_end:
        # Capture frame-by-frame
        code = recogniser.wait_next()
        print(code)
        display.write_text(code)
    display.free()
    recogniser.free()


def signal_handler(signal, frame):
    global is_end
    is_end = True


signal.signal(signal.SIGINT, signal_handler)

if __name__ == "__main__":
    main()
