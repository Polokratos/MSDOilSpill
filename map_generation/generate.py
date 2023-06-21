import sys
from PIL import Image

land = (251, 251, 20)
white = (242, 247, 252)
water_light_gray = (162, 173, 178)
water_gray = (210, 232, 249)
water_dark_gray = (139, 157, 172)
light_blue = (208, 231, 248)
black = (0, 0, 0)
rig_red = (210, 44, 42)

l = "1"
s = "2"
w = "3"

pixel_type = {
        l: (land, black),
        w: (water_gray, light_blue, rig_red)
    }

def match_type(r, g, b):
    result = None
    closest_distance = 1000000

    for key, values in pixel_type.items():
        for colors in values:
            dist = (r - colors[0]) ** 2 + (g - colors[1]) ** 2 + (b - colors[2])
            if dist < closest_distance:
                closest_distance = dist
                result = key

    return result

def png_to_map(image_path):
    image = Image.open(image_path)
    rgb_image = image.convert("RGB")
    width, height = rgb_image.size
    result = []

    for y in range(height):
        for x in range(width):
            r, g, b = rgb_image.getpixel((x, y))
            result.append(match_type(r, g, b))

    # fix land
    for y in range(height):
        for x in range(width):
            if result[y * width + x] == l: continue

            water_neighbours = 0
            for y_delta in (-1, 0, 1):
                new_y = y + y_delta
                if new_y < 0 or new_y == height: continue

                for x_delta in (-1, 0, 1):
                    new_x = x + x_delta
                    if new_x < 0 or new_x == width: continue

                    if result[new_y * width + new_x] == w:
                        water_neighbours += 1

            if water_neighbours < 5: result[y * width + x] = l

    for y in range(height):
        for x in range(width):
            if result[y * width + x] == w: continue

            is_shore = False
            for y_delta in (-1, 0, 1):
                new_y = y + y_delta
                if new_y < 0 or new_y == height: continue

                for x_delta in (-1, 0, 1):
                    new_x = x + x_delta
                    if new_x < 0 or new_x == width: continue

                    if result[new_y * width + new_x] == w:
                        is_shore = True
                        break

            if is_shore: result[y * width + x] = s
    

    return result



if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Program accepts 1 argument - path to a file")
        exit(1)
    processed = png_to_map(sys.argv[1])


    with open("mapa.txt", "w") as f:
        f.write(" ".join(processed))
