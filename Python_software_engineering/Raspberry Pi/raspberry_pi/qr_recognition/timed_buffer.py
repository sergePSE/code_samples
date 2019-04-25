import time


class TimedBuffer:
    def __init__(self, timeout_sec):
        self._buffer = {}
        self._timeout = timeout_sec

    def add(self, record):
        self._clean_buffer()
        if record not in self._buffer:
            self._buffer[record] = time.time()
            return True
        return False

    def _clean_buffer(self):
        records = list(self._buffer.keys())
        for record in records:
            now = time.time()
            if now - self._buffer[record] > self._timeout:
                del self._buffer[record]
