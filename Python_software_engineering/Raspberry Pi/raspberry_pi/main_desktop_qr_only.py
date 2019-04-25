import signal
import settings
from qr_recognition.recogniser import QRCodeRecogniser
from qr_recognition.timed_buffer import TimedBuffer
from unittest.mock import MagicMock


is_end = False


# due to lack of support this version has piface omitted
# just for the functional test of qr code recognition. See qr code results online.
def main():
    print("Press ctrl-C to exit")
    display = MagicMock()
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
