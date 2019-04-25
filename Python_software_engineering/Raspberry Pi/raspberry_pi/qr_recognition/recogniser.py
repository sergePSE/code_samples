import qr_recognition.camera
from pyzbar.pyzbar import decode
import settings


class QRCodeRecogniser:
    # Buffer stores data. If add returns true -> success of safe, false -> a repetition of data.
    def __init__(self, buffer, is_debug=None):
        if is_debug is None:
            self._is_debug = settings.is_debug
        else:
            self._is_debug = is_debug
        self._buffer = buffer

        self._camera = qr_recognition.camera.Camera()

    def _decode_frame(self, frame):
        decode_result = decode(frame)
        for code in decode_result:
            try:
                if self._buffer.add(code.data):
                    str_data = code.data.decode("utf-8")
                    return str_data
            except UnicodeDecodeError:
                print("can not parse data: {} has errors".format(code))
        return None

    # Decodes the next frame in process. Can be used with a single qr code at the time.
    # Returns str if QR was found or none.
    def wait_next(self):
        # Capture frame-by-frame
        frame = self._camera.take_photo()
        decode_result = self._decode_frame(frame)
        # Our operations on the frame come here
        return decode_result

    def free(self):
        self._camera.free()
