import pygame
import pygame.camera
from PIL import Image


HD = (640, 480)


class Camera:
    def __init__(self):
        pygame.init()
        pygame.camera.init()
        camlist = pygame.camera.list_cameras()
        if camlist:
            self._cam = pygame.camera.Camera(camlist[0], HD, "RGB")
            self._cam.start()
        else:
            raise ModuleNotFoundError("Not camera is found")

    def take_photo(self):
        pil_string_image = pygame.image.tostring(self._cam.get_image(), "RGBA", False)
        return Image.frombytes("RGBA", HD, pil_string_image)

    def free(self):
        if self._cam:
            self._cam.stop()
        pygame.camera.quit()
        pygame.quit()
