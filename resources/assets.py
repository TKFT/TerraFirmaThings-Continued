from mcresources import ResourceManager

from constants import SURVEYORS_HAMMER_HEAD_MOLD_PATTERN


def generate_assets(rm: ResourceManager) -> None:
    rm.custom_block_model(
        'mold/surveyors_hammer_head_mold',
        'tfc:mold',
        {
            'textures': {
                '0': 'tfc:block/mold',
                'particle': 'tfc:block/mold'
            },
            'pattern': SURVEYORS_HAMMER_HEAD_MOLD_PATTERN
        }
    )