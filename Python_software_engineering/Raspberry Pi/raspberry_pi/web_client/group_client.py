import requests
import settings


GROUP_FINDER_WEB_PATH = "/groupFinder"


def find_group(student_id, tutor_name):
    url = settings.host + GROUP_FINDER_WEB_PATH
    response = requests.get(url, {"student_id": student_id, "tutor_name": tutor_name})
    if response.ok:
        return response.text
    print("Group find fails, code {}. student_id: {}, tutor_name: {}"
          .format(response.status_code, student_id, tutor_name))
    return None
