import os
from xml.dom import minidom

def getText(nodelist):
	rc = []
	for node in nodelist:
		if node.nodeType == node.TEXT_NODE:
			rc.append(node.data)
	return ''.join(rc)


blockConf = minidom.parse('../conf/blocks.conf')

usedMats = set()

# Find materials used in blocks.conf
for matNode in blockConf.getElementsByTagName('materials'):
	matList = getText(matNode.childNodes)
	matArr = matList.split(',')
	for mat in matArr:
		usedMats.add(mat.strip())
# also check mesh materials
for meshNode in blockConf.getElementsByTagName('mesh'):
	mat = meshNode.getAttribute('jmc_material')
	if mat != "":
		usedMats.add(mat)

# Find materials used in obj models
for file in os.scandir("../conf/models"):
	if file.path.endswith(".obj") and file.is_file():
		with open(file) as obj:
			for line in obj.readlines():
				if line.startswith("usemtl "):
					usedMats.add(line.replace("usemtl ", "").strip())

currMat = "nul"
mtlMats = {}

# Find materials defined in mtl
with open("../conf/default.mtl") as mtlFile:
	for line in mtlFile.readlines():
		if line.startswith("newmtl "):
			currMat = line.replace("newmtl ", "").strip()
			if currMat in mtlMats.keys():
				print("'%s' mtl duplicate!" % currMat)
				continue
			mtlMats[currMat] = {"name":currMat}
		elif line.startswith("Kd "):
			mtlMats[currMat]["Kd"] = line.replace("Kd ", "").strip()
		elif line.startswith("Ks "):
			mtlMats[currMat]["Ks"] = line.replace("Ks ", "").strip()
		elif line.startswith("map_Kd "):
			mtlMats[currMat]["map_Kd"] = line.replace("map_Kd ", "").strip()
		elif line.startswith("map_d "):
			mtlMats[currMat]["map_d"] = line.replace("map_d ", "").strip()

# Check if mat is defined in mtl
for mat in usedMats:
	if mat not in mtlMats.keys():
		print("'%s' not in mtl" % mat)

# Check if defined mats are not used
print("----------------------------------------")
for mtlMat in mtlMats.values():
	if mtlMat["name"] not in usedMats:
		print("'%s' in mtl but not used" % mtlMat["name"])

# Check defined mat textures exist (saved from texsplit)
print("----------------------------------------")
for mtlMat in mtlMats.values():
	try:
		if not os.path.exists("../../jmcTest/" + mtlMat["map_Kd"]):
			print("'%s' tex not found. %s" % (mtlMat["name"], mtlMat["map_Kd"]))
	except KeyError as e:
		print("'%s' missing map_Kd" % mtlMat["name"])
