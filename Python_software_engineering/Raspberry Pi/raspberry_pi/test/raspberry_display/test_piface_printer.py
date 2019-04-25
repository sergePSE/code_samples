from unittest import mock
from unittest.mock import call
import unittest
from time import sleep
from raspberry_display.piface_printer import PIFacePrinter


class PiFacePrinterTest(unittest.TestCase):
    permanent_message = "first message"
    temporary_message = "second message"
    seconds_refresh = 0.1

    @mock.patch('pifacecad.PiFaceCAD')
    def test_cycle(self, cad):
        printer = PIFacePrinter(self.seconds_refresh, cad)
        printer.write_permanent(self.permanent_message)
        printer.write_text(self.temporary_message)
        sleep(self.seconds_refresh * 10)
        calls = [call(PiFacePrinterTest.permanent_message), call(PiFacePrinterTest.temporary_message),
                 call(PiFacePrinterTest.permanent_message)]
        printer._cad.lcd.write.assert_has_calls(calls, False)


if __name__ == '__main__':
    unittest.main()
