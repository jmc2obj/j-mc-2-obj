import maya.cmds as cmds
import maya.mel as mel

def convertname(name:str) -> str:
    convertT = {58:45}
    return str(name.translate(convertT))

def adjustPath(Org_Path:str,addto:str):

    '''
    Adjust the given path to correctly save the new file.
    '''

    path = Org_Path.split("\\")
    file = path[-1]
    filename = file.split(".")[0]
    fileext = file.split(".")[-1]

    newfilename = filename + "_" + addto + "." + fileext
    path.pop(-1)
    path.append(newfilename)

    newpath = '\\'.join(path)

    return newpath

def objHandler(fileP) -> str:
    newOBJ = []
    newpath = adjustPath(fileP,"ConvMaya")
    with open(fileP,"r") as obj:
        for i in obj:
            if i[0] == "o":
                i = i.replace("o","g",1)
                newOBJ.append(str(i))
            else:
                newOBJ.append(str(i))

    f = open(newpath,"a")
    
    for i in newOBJ:
        f.write(i)
    
    f.close

    return newpath

if __name__=="__main__":
    filepath = cmds.fileDialog2(fileMode=1,fileFilter= "OBJ File(*.obj)")
    Newpath = objHandler(filepath[0])
    command = 'file -import -type "OBJ"  -ignoreVersion -ra true -mergeNamespacesOnClash false -namespace "Obj" -options "mo=1;lo=0"  -pr  -importFrameRate true  -importTimeRange "override" "'+str(Newpath)+'";'
    mel.eval(command)