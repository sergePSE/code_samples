from unittest.mock import MagicMock
from unittest import mock
import settings
import unittest


from frame_processing.raspberry_processor import RaspberryProcessor, M_STAND_BY


def _get_processor():
        recogniser = MagicMock()
        display = MagicMock()
        processor = RaspberryProcessor(recogniser, display)
        return processor, recogniser, display


class RaspberryProcessorTest(unittest.TestCase):

    @mock.patch('web_client.attendance_client.register_attendance')
    def test_normal_read(self, register_attendance_func):
        processor, recogniser, display = _get_processor()
        display.wait_button_press.return_value = True
        recogniser.wait_next.return_value = "4785074604081152185804764220139124118"
        register_attendance_func.return_value = "404: group 6192449487634432 does not exist"
        processor.process_next_frame()
        register_attendance_func.assert_called_once_with("4764220139124118", "478507460408115218580", settings, True)
        display.write_permanent.assert_called_with(M_STAND_BY)
        display.write_text.assert_called_with(register_attendance_func.return_value)


if __name__ == '__main__':
    unittest.main()
