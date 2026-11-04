import bpy,sys,json,importlib.util
from pathlib import Path
root=Path(sys.argv[sys.argv.index('--')+1]);folder=root/'debug-export'
addon=Path(__file__).resolve().parents[1]/'conf/contrib/jmc2obj_single_rgba.py'
bpy.ops.wm.read_factory_settings(use_empty=True)
bpy.ops.wm.obj_import(filepath=str(folder/'minecraft.obj'))
def report():
 result=[]
 for mat in bpy.data.materials:
  if not mat.use_nodes:continue
  for shader in mat.node_tree.nodes:
   if shader.type!='BSDF_PRINCIPLED':continue
   color=shader.inputs['Base Color'];alpha=shader.inputs['Alpha']
   if color.is_linked and alpha.is_linked:
    c=color.links[0];a=alpha.links[0]
    result.append({'material':mat.name,'image_nodes':sum(n.type=='TEX_IMAGE' for n in mat.node_tree.nodes),'same_node':c.from_node==a.from_node,'color_socket':c.from_socket.name,'alpha_socket':a.from_socket.name})
 return result
before=report();assert before and any(not r['same_node'] for r in before)
spec=importlib.util.spec_from_file_location('jmc2obj_single_rgba',addon);module=importlib.util.module_from_spec(spec);spec.loader.exec_module(module);module.register()
after=report();assert len(after)==len(before) and all(r['same_node'] and r['image_nodes']==1 and r['alpha_socket']=='Alpha' for r in after)
# Animate only the color node's UV input. Both color and opacity must stay on it.
mat=next(m for m in bpy.data.materials if 'seagrass' in m.name);shader=next(n for n in mat.node_tree.nodes if n.type=='BSDF_PRINCIPLED');image=shader.inputs['Base Color'].links[0].from_node
mapping=mat.node_tree.nodes.new('ShaderNodeMapping');coord=mat.node_tree.nodes.new('ShaderNodeTexCoord');mat.node_tree.links.new(coord.outputs['UV'],mapping.inputs['Vector']);mat.node_tree.links.new(mapping.outputs['Vector'],image.inputs['Vector'])
for frame,y in [(1,0),(2,.0625)]:mapping.inputs['Location'].default_value[1]=y;mapping.inputs['Location'].keyframe_insert('default_value',frame=frame)
for frame in [1,2]:
 bpy.context.scene.frame_set(frame);assert shader.inputs['Alpha'].links[0].from_node==image
# Fresh default OBJ import triggers the module via depsgraph; no manual repair call.
bpy.ops.wm.read_factory_settings(use_empty=True);bpy.ops.wm.obj_import(filepath=str(folder/'minecraft.obj'));bpy.context.view_layer.update()
automatic=report();assert automatic and all(r['same_node'] and r['image_nodes']==1 for r in automatic)
# Protect unrelated material graphs.
other=bpy.data.materials.new('Unrelated');other.use_nodes=True;shader=other.node_tree.nodes.get('Principled BSDF');first=other.node_tree.nodes.new('ShaderNodeTexImage');second=other.node_tree.nodes.new('ShaderNodeTexImage');first.image=second.image=bpy.data.images.load(str(folder/'tex/minecraft/block/seagrass.png'),check_existing=True);other.node_tree.links.new(first.outputs['Color'],shader.inputs['Base Color']);other.node_tree.links.new(second.outputs['Alpha'],shader.inputs['Alpha']);module._after_import();assert shader.inputs['Alpha'].links[0].from_node==second
bpy.data.materials.remove(other)
bpy.ops.file.pack_all();bpy.ops.wm.save_as_mainfile(filepath=str(root/'single-rgba-verified.blend'))
(root/'blender-verification.json').write_text(json.dumps({'before':before,'after':after,'automatic_after_fresh_import':automatic,'color_uv_animation_affects_same_alpha_node':True,'unrelated_material_untouched':True},indent=2))
print('PASS',len(after),'transparent materials: one image node, Color -> Base Color, Alpha -> Alpha; automatic fresh OBJ import and UV animation test')
