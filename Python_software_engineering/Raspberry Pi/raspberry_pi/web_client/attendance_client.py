import requests
import http.client
from http import HTTPStatus
import xml.etree.ElementTree as ET

from web_client import group_client

ATTENDANCE_WEB_PATH = "/rest/attendances/"


def _get_attendance_response_text(response):
    if response.ok:
        return response.text
    if response.status_code in http.client.responses:
        return http.client.responses[response.status_code]
    return str(response.status_code)


def _forge_attendance_record(attendance_id, student_id, settings, is_present):
    try:
        group_id = group_client.find_group(student_id, settings.tutor_name)
    except requests.exceptions.RequestException as e:
        print(e)

    if group_id is None:
        return None
    attendance = ET.Element('attendance')
    ET.SubElement(attendance, 'attendance_id').text = str(attendance_id)
    ET.SubElement(attendance, 'student_id').text = str(student_id)
    ET.SubElement(attendance, 'group_id').text = str(group_id)
    ET.SubElement(attendance, 'week_id').text = str(settings.week_id)
    if is_present:
        ET.SubElement(attendance, 'presented').text = "true"
    else:
        ET.SubElement(attendance, 'presented').text = "false"
    return ET.tostring(attendance).decode()


def register_attendance(attendance_id, student_id, settings, is_present):
    body_message = _forge_attendance_record(attendance_id, student_id, settings, is_present)
    if body_message is None:
        return http.client.responses[HTTPStatus.NOT_FOUND.value]
    try:
        response = requests.post(settings.host + ATTENDANCE_WEB_PATH, data=body_message)
    except requests.exceptions.RequestException as e:
        print(e)
        return e.strerror
    return _get_attendance_response_text(response)
