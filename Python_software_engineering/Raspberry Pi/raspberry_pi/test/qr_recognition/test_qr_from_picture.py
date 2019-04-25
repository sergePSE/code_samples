from PIL import Image
from qr_recognition.recogniser import QRCodeRecogniser
from qr_recognition.timed_buffer import TimedBuffer
import unittest


class TestQrFromPicture(unittest.TestCase):
    def test_photo_recognition(self):
        im = Image.open("test/qr_recognition/qr_pictures/lifePhoto.jpg")
        recogniser = QRCodeRecogniser(TimedBuffer(10))
        code = recogniser._decode_frame(im)
        self.assertEqual("222222222222222222222112345678912345678911", code)
