import time
from time import strftime


def _fields_exist(tree_element):
    for field in [Attendance.f_attendance_id, Attendance.f_course_name, Attendance.f_group_id,
                  Attendance.f_student_name, Attendance.f_time, Attendance.f_tutor_id]:
        elem = tree_element.find(field)
        if elem is None:
            return "Parse XML error: no {} field".format(field)
    return True


def _map_xml(tree):
    time_epoch_text = tree.find(Attendance.f_time).text
    if not time_epoch_text.isnumeric():
        return "Invalid time parse: {}".format(time_epoch_text)

    attendance = Attendance()
    attendance.attendance_id = tree.find(Attendance.f_attendance_id).text
    attendance.student_name = tree.find(Attendance.f_student_name).text
    attendance.group_name = tree.find(Attendance.f_group_id).text
    attendance.tutor_name = tree.find(Attendance.f_tutor_id).text
    attendance.course_name = tree.find(Attendance.f_course_name).text
    attendance.time_str = strftime("%d.%m %H:%M", time.localtime(int(time_epoch_text) / 1000))
    return attendance


def from_XML(tree_element):
    check_result = _fields_exist(tree_element)
    if check_result is not True:
        return check_result
    return _map_xml(tree_element)


class Attendance:
    def __init__(self):
        self.attendance_id = None
        self.student_name = None
        self.group_name = None
        self.tutor_name = None
        self.course_name = None
        self.time_str = None

    f_attendance_id = "attendanceId"
    f_student_name = "studentId"
    f_group_id = "groupId"
    f_tutor_id = "tutorId"
    f_time = "timeEpoh"
    f_course_name = "courseId"

    def to_string(self):
        return "{0} {1} \n {2} {3}".format(self.student_name, self.group_name, self.course_name, self.time_str)
