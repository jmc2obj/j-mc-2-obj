from pathlib import Path
import struct,gzip,json,math,time,zlib
root=Path('work/sprites-v6');world=root/'Мир отладки';(world/'region').mkdir(parents=True,exist_ok=True)
def tag(t,v):return (t,v)
def string(s):
 b=s.encode('utf-8');return struct.pack('>H',len(b))+b
def payload(t,v):
 if t in [1,2,3,4,5,6]:return struct.pack({1:'>b',2:'>h',3:'>i',4:'>q',5:'>f',6:'>d'}[t],v)
 if t==7:return struct.pack('>i',len(v))+v
 if t==8:return string(v)
 if t==9:return bytes([v[0]])+struct.pack('>i',len(v[1]))+b''.join(payload(v[0],x) for x in v[1])
 if t==10:return b''.join(bytes([typ])+string(n)+payload(typ,val) for n,(typ,val) in v.items())+b'\0'
 if t in [11,12]:return struct.pack('>i',len(v))+b''.join(struct.pack('>I' if t==11 else '>Q',x&((1<<(32 if t==11 else 64))-1)) for x in v)
 raise ValueError(t)
def nbt(d):return b'\x0a\x00\x00'+payload(10,d)
def packed(values,bits):
 per=64//bits;return [sum(v<<(i*bits) for i,v in enumerate(values[a:a+per])) for a in range(0,len(values),per)]
blocks=json.loads((root/'minecraft-reports/reports/blocks.json').read_text());states=[None]*sum(len(b['states']) for b in blocks.values())
for name,b in blocks.items():
 for s in b['states']:states[s['id']]={'Name':tag(8,name),**({'Properties':tag(10,{k:tag(8,v) for k,v in s['properties'].items()})} if s.get('properties') else {})}
assert all(states);gw=math.ceil(math.sqrt(len(states)));gh=math.ceil(len(states)/gw)
air={'Name':tag(8,'minecraft:air')};barrier={'Name':tag(8,'minecraft:barrier')};positions=[]
for i,s in enumerate(states):positions.append({'id':s['Name'][1],'state':{k:v[1] for k,v in s.get('Properties',(10,{}))[1].items()},'x':(i//gw)*2+1,'y':70,'z':(i%gw)*2+1})
(root/'debug-block-positions.json').write_text(json.dumps(positions,ensure_ascii=False),encoding='utf-8')
region=bytearray(8192);sector=2;chunks=0
for cz in range(22):
 for cx in range(22):
  palette=[air];lookup={json.dumps(air):0};values=[0]*4096;heights=[61]*256
  for lz in range(16):
   for lx in range(16):
    x=cx*16+lx;z=cz*16+lz
    if x>0 and z>0 and x%2 and z%2 and x//2<=gw and z//2<=gh:
     i=(x//2)*gw+z//2
     if i<len(states):
      s=states[i];key=json.dumps(s)
      if key not in lookup:lookup[key]=len(palette);palette.append(s)
      values[6*256+lz*16+lx]=lookup[key];heights[lz*16+lx]=71
  floor=[0]*4096;floor[12*256:13*256]=[1]*256
  sections=[]
  for sy,ps,vs in [(3,[air,barrier],floor),(4,palette,values)]:
   data={'palette':tag(9,(10,ps))}
   if len(ps)>1:data['data']=tag(12,packed(vs,max(4,(len(ps)-1).bit_length())))
   sections.append({'Y':tag(1,sy),'block_states':tag(10,data),'biomes':tag(10,{'palette':tag(9,(8,['minecraft:plains']))}),'SkyLight':tag(7,b'\xff'*2048),'BlockLight':tag(7,b'\0'*2048)})
  heightdata=packed([h+64 for h in heights],9)
  chunk={'DataVersion':tag(3,4671),'xPos':tag(3,cx),'zPos':tag(3,cz),'yPos':tag(3,-4),'Status':tag(8,'minecraft:full'),'LastUpdate':tag(4,0),'InhabitedTime':tag(4,0),'isLightOn':tag(1,1),'sections':tag(9,(10,sections)),'block_entities':tag(9,(10,[])),'block_ticks':tag(9,(10,[])),'fluid_ticks':tag(9,(10,[])),'Heightmaps':tag(10,{n:tag(12,heightdata) for n in ['WORLD_SURFACE','MOTION_BLOCKING','MOTION_BLOCKING_NO_LEAVES','OCEAN_FLOOR']})}
  compressed=zlib.compress(nbt(chunk));body=struct.pack('>I',len(compressed)+1)+b'\x02'+compressed;nsec=math.ceil(len(body)/4096);idx=cx+cz*32
  region[idx*4:idx*4+4]=((sector<<8)|nsec).to_bytes(4,'big');region[4096+idx*4:4100+idx*4]=int(time.time()).to_bytes(4,'big');region.extend(body+b'\0'*(nsec*4096-len(body)));sector+=nsec;chunks+=1
(world/'region/r.0.0.mca').write_bytes(region)
with __import__('zipfile').ZipFile('work/grass-packs/minecraft.jar') as z:dimension=json.loads(z.read('data/minecraft/worldgen/world_preset/debug_all_block_states.json'))['dimensions']
def convert(v):
 if isinstance(v,dict):return tag(10,{k:convert(x) for k,x in v.items()})
 if isinstance(v,bool):return tag(1,int(v))
 if isinstance(v,int):return tag(3,v)
 return tag(8,v)
data={'DataVersion':tag(3,4671),'version':tag(3,19133),'Version':tag(10,{'Name':tag(8,'1.21.11'),'Id':tag(3,4671),'Snapshot':tag(1,0),'Series':tag(8,'main')}),'LevelName':tag(8,'Мир отладки'),'GameType':tag(3,3),'hardcore':tag(1,0),'allowCommands':tag(1,1),'Difficulty':tag(1,0),'DifficultyLocked':tag(1,1),'initialized':tag(1,1),'Time':tag(4,1000),'DayTime':tag(4,1000),'LastPlayed':tag(4,int(time.time()*1000)),'SpawnX':tag(3,173),'SpawnY':tag(3,80),'SpawnZ':tag(3,173),'spawn':tag(10,{'dimension':tag(8,'minecraft:overworld'),'pos':tag(11,[173,80,173]),'angle':tag(5,0.0),'pitch':tag(5,45.0)}),'WorldGenSettings':tag(10,{'seed':tag(4,0),'generate_features':tag(1,0),'bonus_chest':tag(1,0),'dimensions':convert(dimension)}),'DataPacks':tag(10,{'Enabled':tag(9,(8,['vanilla'])),'Disabled':tag(9,(8,[]))}),'GameRules':tag(10,{'doDaylightCycle':tag(8,'false'),'doMobSpawning':tag(8,'false')})}
(world/'level.dat').write_bytes(gzip.compress(nbt({'Data':tag(10,data)})))
(root/'debug-world-verification.json').write_text(json.dumps({'version':'1.21.11','world_name':'Мир отладки','state_count':len(states),'registered_blocks':len(blocks),'grid_width':gw,'grid_height':gh,'chunks':chunks,'block_y':70,'barrier_y':60,'source':'Minecraft 1.21.11 native data generator blocks.json; DebugLevelSource grid formula; vanilla debug_all_block_states world preset','created_by':'Anvil/NBT writer; not launched in game'},ensure_ascii=False,indent=2),encoding='utf-8')
print('CREATED',world.resolve(),'states',len(states),'chunks',chunks,'bytes',len(region))
