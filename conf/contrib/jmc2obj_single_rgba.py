"""Keep JMC2OBJ color and opacity on one Image Texture node."""
bl_info = {'name': 'JMC2OBJ — Single RGBA Texture', 'author': 'Flok-mk', 'version': (1, 0, 0), 'blender': (3, 6, 0), 'location': 'Automatic after OBJ import; F3: JMC2OBJ: Use PNG Alpha', 'description': 'Connect Alpha from the color spritesheet and remove the redundant opacity node', 'category': 'Import-Export'}

import os
import bpy
from bpy.app.handlers import persistent

_busy = False

def _path(image):
    return os.path.normcase(os.path.normpath(bpy.path.abspath(image.filepath, library=image.library))) if image else ''

def fix_material(material):
    if not material.use_nodes or not material.node_tree:
        return 0
    if not material.name.startswith(('minecraft_', 'jmc2obj_')):
        return 0
    nodes = material.node_tree.nodes
    links = material.node_tree.links
    changed = 0
    for shader in list(nodes):
        if shader.type != 'BSDF_PRINCIPLED':
            continue
        color = shader.inputs.get('Base Color')
        alpha = shader.inputs.get('Alpha')
        if not color or not alpha or not color.is_linked:
            continue
        source = color.links[0].from_node
        if source.type != 'TEX_IMAGE' or not source.image or not alpha.is_linked:
            continue
        opacity = alpha.links[0].from_node
        if opacity == source or opacity.type != 'TEX_IMAGE' or not opacity.image:
            continue
        base_path = _path(source.image)
        opacity_path = _path(opacity.image)
        stem, ext = os.path.splitext(base_path)
        if opacity.image != source.image and opacity_path not in (base_path, stem + '_a' + ext):
            continue
        # Follow the color node's own UV/image animation state, including add-ons.
        links.new(source.outputs['Alpha'], alpha)
        changed += 1
        # Preserve an auxiliary node if another shader still uses it.
        if not any(socket.is_linked for socket in opacity.outputs):
            nodes.remove(opacity)
        nodes.active = source
        source.select = True
    return changed

@persistent
def _after_import(*_args):
    global _busy
    if _busy or not hasattr(bpy.data, "materials"):
        return
    _busy = True
    try:
        for material in bpy.data.materials:
            fix_material(material)
    finally:
        _busy = False

def _initial_fix():
    _after_import()
    return None

class JMC2OBJ_OT_use_png_alpha(bpy.types.Operator):
    bl_idname = 'jmc2obj.use_png_alpha'
    bl_label = 'JMC2OBJ: Use PNG Alpha'
    bl_description = 'Use Alpha from the color spritesheet in Minecraft materials'
    bl_options = {'REGISTER', 'UNDO'}

    def execute(self, context):
        count = sum(fix_material(mat) for mat in bpy.data.materials)
        self.report({'INFO'}, 'JMC2OBJ: fixed %d material connections' % count)
        return {'FINISHED'}

def register():
    bpy.utils.register_class(JMC2OBJ_OT_use_png_alpha)
    if _after_import not in bpy.app.handlers.depsgraph_update_post:
        bpy.app.handlers.depsgraph_update_post.append(_after_import)
    if _after_import not in bpy.app.handlers.load_post:
        bpy.app.handlers.load_post.append(_after_import)
    _after_import()
    bpy.app.timers.register(_initial_fix, first_interval=0.1)

def unregister():
    if bpy.app.timers.is_registered(_initial_fix):
        bpy.app.timers.unregister(_initial_fix)
    for handlers in (bpy.app.handlers.depsgraph_update_post, bpy.app.handlers.load_post):
        if _after_import in handlers:
            handlers.remove(_after_import)
    bpy.utils.unregister_class(JMC2OBJ_OT_use_png_alpha)

if __name__ == '__main__':
    register()
