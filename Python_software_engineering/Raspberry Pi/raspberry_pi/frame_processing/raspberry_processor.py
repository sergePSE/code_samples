import web_client.attendance_client
import settings


M_STAND_BY = "Waiting QR code"
M_PRESS_BUTTON = "Button: 1 - Yes, 2 - No"
M_BUTTON_WAIT = "Yes - 1, No - 2"


# returns (attendance, student) ids or None if not appropriate
# code_text should be in a format of (student_id:21symbol_attendanceToken:rest) no space between
def _recognise_code(code_text):
    if len(code_text) <= 21:
        print("Error recognising qr code:{}".format(code_text))
        return None, None
    return code_text[21:], code_text[:21]


class RaspberryProcessor:
    def __init__(self, recogniser, display):
        self._recogniser = recogniser
        self._display = display
        self._display.write_permanent(M_STAND_BY)

    def _process_attendance_code(self, attendance_id, student_id, is_present):
        register_response = web_client.attendance_client.register_attendance(attendance_id, student_id, settings,
                                                                             is_present)
        if register_response is not None:
            return self._display.write_text(register_response)

    def process_next_frame(self):
        text_result = self._recogniser.wait_next()
        if text_result is None:
            return
        attendance_id, student_id = _recognise_code(text_result)
        if attendance_id is None or student_id is None:
            return
        self._display.write_permanent(M_BUTTON_WAIT)
        self._display.write_text(M_PRESS_BUTTON)
        is_present = self._display.wait_button_press()
        # TODO debug
        print("is present " + str(is_present))
        self._display.write_permanent(M_STAND_BY)
        self._process_attendance_code(attendance_id, student_id, is_present)
