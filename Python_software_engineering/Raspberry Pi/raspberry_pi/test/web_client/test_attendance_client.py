import unittest
from unittest import mock
from http import HTTPStatus
from web_client.attendance_client import register_attendance
from test.web_client.mock_http_response import MockHttpResponse
import settings


attendance_example = '<attendance>\
   <attendance_id>4785074604081152</attendance_id>\
   <student_id>185804764220139124118</student_id>\
   <group_id>6192449487634432</group_id>\
   <week_id>0</week_id>\
   <presented>true</presented>\
</attendance>'


class AttendanceClientTest(unittest.TestCase):
    @mock.patch('requests.post')
    @mock.patch('web_client.attendance_client.group_client')
    def test_post_success(self, group_client, post):
        response = MockHttpResponse(HTTPStatus.OK.value, "200: OK")
        post.return_value = response
        settings.group_id = 6192449487634432
        group_client.find_group.return_value = settings.group_id
        settings.week_id = 0
        register_attendance("4785074604081152", "185804764220139124118", settings, True)
        url, data = post.call_args
        self.assertEqual(attendance_example.replace("\n", "").replace("\r", "").replace(" ", ""), data['data'])

    @mock.patch('requests.post')
    @mock.patch('web_client.attendance_client.group_client')
    def test_post_fail(self, group_client, post):
        response = MockHttpResponse(HTTPStatus.NOT_FOUND.value)
        group_client.find_group.return_value = settings.group_id
        post.return_value = response
        attendance_status = register_attendance("any text", "any token", settings, True)
        self.assertEqual(attendance_status, "Not Found")


if __name__ == '__main__':
    unittest.main()
