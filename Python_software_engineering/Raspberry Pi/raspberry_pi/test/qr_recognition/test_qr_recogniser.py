from unittest import mock
import unittest
from time import sleep
from qr_recognition.recogniser import QRCodeRecogniser
from qr_recognition.timed_buffer import TimedBuffer
import pygame
from PIL import Image


qr_samples_folder = "test/qr_recognition/qr_pictures/"


def get_picture_path(name):
    return qr_samples_folder + name + ".png"


class QRCodeRecogniserTest(unittest.TestCase):
    tutor_token = "tTutorTempTocken1"
    attendance_id = "aattandanceIdRecord1"
    timeout_seconds = 1

    @mock.patch('qr_recognition.camera.Camera')
    def test_repeat_read(self, camera):
        path = get_picture_path(self.attendance_id)
        camera.take_photo.return_value = pygame.image.load(path)
        recogniser = QRCodeRecogniser(TimedBuffer(self.timeout_seconds), is_debug=False)
        pil_string_image = pygame.image.tostring(pygame.image.load(path), "RGBA", False)
        recogniser._camera.take_photo.return_value = Image.frombytes("RGBA", (300, 300), pil_string_image)
        # test qr code is right
        result = recogniser.wait_next()
        self.assertEqual(result, self.attendance_id)

        # test if can read same token within timeout
        self.assertIsNone(recogniser.wait_next())

        # test if can read again after the refresh time
        sleep(self.timeout_seconds)
        self.assertEqual(recogniser.wait_next(), self.attendance_id)


if __name__ == '__main__':
    unittest.main()
