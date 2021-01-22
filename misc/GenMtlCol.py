import os, re
from PIL import Image

EXPORT_DIR = "../../jmcTest/"

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
			kdValsStr = re.match(r"Kd (\d*\.?\d+?) (\d*\.?\d+?) (\d*\.?\d+?)", line).groups()
			mtlMats[currMat]["Kd"] = [float(kdValsStr[0]), float(kdValsStr[1]), float(kdValsStr[2])]
		elif line.startswith("Ks "):
			mtlMats[currMat]["Ks"] = line.replace("Ks ", "").strip()
		elif line.startswith("map_Kd "):
			mtlMats[currMat]["map_Kd"] = line.replace("map_Kd ", "").strip()
		elif line.startswith("map_d "):
			mtlMats[currMat]["map_d"] = line.replace("map_d ", "").strip()

newMtlLines = []

for mtlName, mtlMat in sorted(mtlMats.items()):
	if "map_Kd" in mtlMat.keys() and os.path.exists(EXPORT_DIR + mtlMat["map_Kd"]):
		im = Image.open(EXPORT_DIR + mtlMat["map_Kd"])
		avg = [0, 0, 0]
		num = 0
		hasAlpha = False
		for pixel in im.getdata():
			if pixel[3] < 255:
				hasAlpha = True
				if pixel[3] == 0:
					continue
			avg[0] += pixel[0] / 255
			avg[1] += pixel[1] / 255
			avg[2] += pixel[2] / 255
			num += 1
		avg[0] /= num
		avg[1] /= num
		avg[2] /= num
		mtlMat["Kd"] = avg
		if hasAlpha:
			mtlMat["map_d"] = mtlMat["map_Kd"]
		print("%s: %s alpha: %s" % (mtlMat["name"], avg, hasAlpha))
	
	if mtlName == "water":
		mtlMat["Kd"] = [0.0, 0.6, 1.0]
	
	newMtlLines.append("newmtl %s\n" % mtlMat["name"])
	newMtlLines.append("Kd %6.4f %6.4f %6.4f\n" % tuple(mtlMat["Kd"]))
	if "Ks" in mtlMat.keys():
		newMtlLines.append("Ks %s\n" % mtlMat["Ks"])
	if "map_Kd" in mtlMat.keys():
		newMtlLines.append("map_Kd %s\n" % mtlMat["map_Kd"])
	if "map_d" in mtlMat.keys():
		newMtlLines.append("map_d %s\n" % mtlMat["map_d"])
	newMtlLines.append("\n")

with open("../conf/default.mtl", "w") as mtlFile:
	mtlFile.writelines(newMtlLines)