from mcresources import ResourceManager

from constants import MOD_ID, RESOURCE_DIR
from assets import generate_assets
from data import generate_data
from generate_textures import generate as generate_textures


def main() -> None:
    generate_textures()

    rm = ResourceManager(MOD_ID, resource_dir=RESOURCE_DIR)
    generate_assets(rm)
    generate_data(rm)
    rm.flush()


if __name__ == '__main__':
    main()