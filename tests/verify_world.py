from pathlib import Path
from argparse import ArgumentParser
import gzip,struct,io,zlib,json
parser = ArgumentParser(description="Validate the generated Minecraft debug save.")
parser.add_argument("block_report", type=Path, help="Minecraft-generated reports/blocks.json")
parser.add_argument("output", type=Path, help="Output directory used by create_debug_world.py")
args = parser.parse_args()
root = args.output
world = root / 'Мир отладки'
def parse(b):
 f=io.BytesIO(b)
 def num(fmt):return struct.unpack(fmt,f.read(struct.calcsize(fmt)))[0]
 def st():return f.read(num('>H')).decode('utf-8')
 def val(t):
  if t in [1,2,3,4,5,6]:return num({1:'>b',2:'>h',3:'>i',4:'>q',5:'>f',6:'>d'}[t])
  if t==7:return f.read(num('>i'))
  if t==8:return st()
  if t==9:
   a=num('>B');return [val(a) for _ in range(num('>i'))]
  if t==10:
   d={}
   while True:
    a=num('>B')
    if a==0:return d
    n=st();d[n]=val(a)
  if t in [11,12]:return [num('>I' if t==11 else '>Q') for _ in range(num('>i'))]
  raise ValueError(t)
 t=num('>B');name=st();return val(t)
level=parse(gzip.decompress((world/'level.dat').read_bytes()))['Data'];assert level['LevelName']=='Мир отладки' and level['WorldGenSettings']['dimensions']['minecraft:overworld']['generator']['type']=='minecraft:debug'
def key(s):return s['Name'],tuple(sorted(s.get('Properties',{}).items()))
expected=json.loads(args.block_report.read_text(encoding='utf-8'));states=set()
for name,data in expected.items():
 for s in data['states']:states.add((name,tuple(sorted(s.get('properties',{}).items()))))
observed=set();r=(world/'region/r.0.0.mca').read_bytes();chunks=0
for i in range(1024):
 loc=int.from_bytes(r[i*4:i*4+4],'big');sector=loc>>8
 if not sector:continue
 pos=sector*4096;n=int.from_bytes(r[pos:pos+4],'big');assert r[pos+4]==2;ch=parse(zlib.decompress(r[pos+5:pos+4+n]));chunks+=1
 for sec in ch['sections']:
  if sec['Y']!=4:continue
  bs=sec['block_states'];pal=bs['palette'];bits=max(4,(len(pal)-1).bit_length());packed=bs.get('data');per=64//bits;mask=(1<<bits)-1
  for z in range(16):
   for x in range(16):
    idx=6*256+z*16+x;paletteidx=0 if packed is None else (packed[idx//per]>>((idx%per)*bits))&mask;observed.add(key(pal[paletteidx]))
assert states<=observed,(len(states-observed),list(states-observed)[:2]);assert chunks==484
p=root/'debug-world-verification.json';report=json.loads(p.read_text(encoding='utf-8'));report.update({'nbt_readback':True,'unique_states_readback':len(states&observed),'missing_states':0,'readback_chunks':chunks});p.write_text(json.dumps(report,ensure_ascii=False,indent=2),encoding='utf-8');print('PASS debug NBT/Anvil readback:',len(states),'of',len(states),'states;',chunks,'chunks')
