import signal
import settings
from frame_processing.raspberry_processor import RaspberryProcessor
from qr_recognition.recogniser import QRCodeRecogniser
from qr_recognition.timed_buffer import TimedBuffer
from raspberry_display.terminal_printer import TerminalPrinter


is_end = False


# almost fully functional version without PI's display. Can be run on desktop. Output is in terminal
def main():
    print("Press ctrl-C to exit")
    display = TerminalPrinter(refresh_speed_sec=settings.display_refresh_time_sec)
    recogniser = QRCodeRecogniser(TimedBuffer(settings.code_repetition_time_sec))
    frame_processor = RaspberryProcessor(recogniser, display)
    while not is_end:
        # Capture frame-by-frame
        frame_processor.process_next_frame()
    recogniser.free()
    display.free()


def signal_handler(signal, frame):
    global is_end
    is_end = True


signal.signal(signal.SIGINT, signal_handler)

if __name__ == "__main__":
    main()
