from pathlib import Path
from argparse import ArgumentParser
from PIL import Image,ImageChops
import json,io,zipfile,re
parser = ArgumentParser(description="Compare animation export against the previous OBJ and source spritesheets.")
parser.add_argument("before", type=Path)
parser.add_argument("after", type=Path)
parser.add_argument("report", type=Path)
parser.add_argument("packs", type=Path, nargs="+", help="Resource packs in export priority order, including the client JAR")
args = parser.parse_args()
before, af = args.before, args.after
packs = [zipfile.ZipFile(pack) for pack in args.packs]
def load(n):
 for z in packs:
  if n in z.namelist():return z.read(n)
 raise KeyError(n)
config_path = Path(__file__).resolve().parents[1] / 'conf/textures.json'
config={d['id']:d for d in json.loads(config_path.read_text(encoding='utf-8'))};records=[];errors=[]
for meta in sorted(af.rglob('*.png.mcmeta')):
 p=meta.with_suffix('');d=json.loads(meta.read_text());im=Image.open(p).convert('RGBA');rel=p.relative_to(af).as_posix();old=Image.open(before/rel).convert('RGBA');src=None;tint=None;denominator=255
 if rel.startswith('tex/minecraft/'):
  tex=rel[len('tex/minecraft/'): -4];src='assets/minecraft/textures/'+tex+'.png';setting=config.get('minecraft:'+tex,{})
  if 'tint' in setting:tint=bytes.fromhex(setting['tint']);denominator=256
 elif '/model/minecraft/' in rel:
  tex=rel.split('/model/minecraft/')[1][:-4];base,suffix=tex.rsplit('_',1);src='assets/minecraft/textures/'+base+'.png';tint=None if suffix=='raw' else bytes.fromhex(suffix)
 if src:
  expected=Image.open(io.BytesIO(load(src))).convert('RGBA')
  if tint:
   expected.putdata([tuple(v[i]*tint[i]//denominator for i in range(3))+(v[3],) for v in expected.getdata()])
  if im.size!=expected.size or any(c.getbbox() for c in ImageChops.difference(im,expected).split()):errors.append(src)
  orig=json.loads(load(src+'.mcmeta'));assert orig==d,(src,'metadata changed')
 records.append({'path':rel,'before':old.size,'after':im.size,'animation':d.get('animation')})
print('Exported animation sheets:',len(records),'pixel differences from source/tint:',errors)
for n in ['water_still','lava_still','nether_portal','fire_0','prismarine','sea_lantern','sculk']:
 print(next((r for r in records if r['path'].endswith('/'+n+'.png')),None))
assert len(records)>=50 and not errors
face_counts={}
for folder in [before,af]:
 mtl=(folder/'minecraft.mtl').read_text();paths=[l.split(' ',1)[1] for l in mtl.splitlines() if l.startswith(('map_Kd ','map_d '))];assert all((folder/p).is_file() for p in paths)
 faces=sum(1 for l in (folder/'minecraft.obj').open() if l.startswith('f '));face_counts[folder.name]=faces;print(folder.name,'faces',faces,'materials',mtl.count('newmtl '))
assert len(set(face_counts.values()))==1
args.report.write_text(json.dumps({'spritesheets':records,'raw_png_mismatches':errors,'faces':face_counts},ensure_ascii=False,indent=2),encoding='utf-8')
