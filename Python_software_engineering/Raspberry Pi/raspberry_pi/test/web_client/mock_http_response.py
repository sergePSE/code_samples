from http import HTTPStatus


class MockHttpResponse:
    def __init__(self, status_code, text = None):
        self.status_code = status_code
        self.text = text
        self.ok = status_code == HTTPStatus.OK.value
