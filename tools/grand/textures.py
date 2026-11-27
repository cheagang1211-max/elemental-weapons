"""대형 기술 입체 모형 텍스처 (속성 7종 × 256 아틀라스, 64칸 8재질)
 BASE(0,0) 몸체(속성별: 철·흑요석·비늘·나무껍질·백석·암흑·대리석) / TRIM(64,0) 장식 금속 / CRYSTAL(128,0) 속성 수정(빛남)
 LIGHT(192,0) 빛 핵(빛남) / CLOTH(0,64) 천·깃털·잎 / CLOTH_GLOW(64,64) 끝이 빛나는 천 / ARMOR(128,64) 갑판(새김 빛) / DARK(192,64) 그림자
"""
import os, random, math
from PIL import Image
OUT = os.path.join(os.path.dirname(__file__), "..", "..", "src/main/resources/assets/elementalweapons/textures/entity")
P = {
 "electric": dict(base=(70,82,140), trim=(246,214,80), crystal=(255,238,90), light=(255,255,210), cloth=(44,54,120), armor=(52,62,104), dark=(16,18,40), kind="metal"),
 "fire":     dict(base=(58,26,22), trim=(255,146,44), crystal=(255,120,30), light=(255,232,150), cloth=(160,42,22), armor=(66,22,16), dark=(24,8,6), kind="lava"),
 "water":    dict(base=(36,112,160), trim=(226,244,255), crystal=(96,222,255), light=(226,250,255), cloth=(30,84,154), armor=(22,64,112), dark=(8,20,44), kind="scale"),
 "grass":    dict(base=(104,74,44), trim=(236,206,128), crystal=(255,172,214), light=(255,244,206), cloth=(66,156,62), armor=(74,112,52), dark=(30,40,20), kind="bark"),
 "wind":     dict(base=(206,236,230), trim=(255,255,255), crystal=(170,255,230), light=(240,255,250), cloth=(156,222,212), armor=(124,182,176), dark=(50,82,80), kind="stone"),
 "dark":     dict(base=(42,24,58), trim=(156,84,226), crystal=(206,96,255), light=(242,206,255), cloth=(32,16,42), armor=(52,30,72), dark=(8,4,12), kind="metal"),
 "light":    dict(base=(240,236,226), trim=(232,186,70), crystal=(255,230,120), light=(255,255,255), cloth=(250,248,240), armor=(222,216,200), dark=(150,128,90), kind="stone"),
}
def mix(a,b,t): return tuple(int(a[i]+(b[i]-a[i])*t) for i in range(3))
def mul(c,f): return tuple(max(0,min(255,int(v*f))) for v in c)

def make(el):
    p=P[el]; r=random.Random(el)
    im=Image.new("RGBA",(256,256),(0,0,0,0)); gl=Image.new("RGBA",(256,256),(0,0,0,0))
    px,gp=im.load(),gl.load()
    def put(x,y,c,glow=False,a=255):
        px[x,y]=c+(255,)
        if glow: gp[x,y]=c+(a,)
    # BASE
    for y in range(64):
        for x in range(64):
            c=mul(p["base"],0.9+r.random()*0.2)
            k=p["kind"]
            if k=="scale":
                yy=y%6; xx=(x+(3 if (y//6)%2 else 0))%6
                if (xx-3)**2+(yy-1)**2*2>9: c=mul(c,0.78)
                if yy==0: c=mix(c,(230,250,255),0.25)
            elif k=="bark":
                if (x*3+int(4*math.sin(y*0.3)))%7==0: c=mul(c,0.6)
            elif k=="lava":
                if (abs(math.sin(x*0.35+y*0.12)*8 - (y%9))<0.8) or (x*7+y*3)%53==0: put(x,y,mix(p["crystal"],p["light"],0.4),True); continue
            elif k=="metal":
                if y%8==0 or x%16==0: c=mul(c,0.7)
                if y%8==1: c=mul(c,1.2)
            else:
                if (x+y*3)%23==0: c=mul(c,0.82)
            put(x,y,c)
    # TRIM: 금속 띠 + 새김
    for y in range(64):
        for x in range(64,128):
            c=mul(p["trim"],1.15-0.35*((y%8)/7))
            if ((x//2)+(y//2))%4==0: c=mul(c,0.78)
            put(x,y,c, glow=(x+y)%9==0, a=90)
    # CRYSTAL
    for y in range(64):
        for x in range(128,192):
            fx=(x%8)/7; fy=(y%8)/7
            c=mul(p["crystal"],1.25 if fx+fy<1 else 0.85)
            c=mix(c,(255,255,255),0.35 if (x%8==1 and y%8==1) else 0)
            put(x,y,c,True)
    # LIGHT
    for y in range(64):
        for x in range(192,256):
            d=abs((x%16)-7.5)+abs((y%16)-7.5)
            c=mix(p["light"],(255,255,255),max(0,0.6-d/16))
            put(x,y,c,True)
    # CLOTH (천/깃털/잎)
    for y in range(64,128):
        for x in range(64):
            c=mul(p["cloth"],1.1-0.3*((y-64)/63))
            if x%4==3: c=mul(c,0.82)
            if el=="grass" and r.random()<0.12: c=mix(c,(150,220,90),0.4)
            if (x%8==4) and el in ("light","wind"): c=mix(c,(255,255,255),0.3)
            put(x,y,c)
    # CLOTH_GLOW: 끝이 빛남
    for y in range(64,128):
        for x in range(64,128):
            t=(y-64)/63
            c=mix(p["cloth"],p["crystal"],max(0,(t-0.55)*2.2))
            if x%4==3: c=mul(c,0.85)
            put(x,y,c, glow=t>0.6, a=int(min(255,(t-0.6)*600)) if t>0.6 else 0)
    # ARMOR: 갑판 + 빛나는 새김
    for y in range(64,128):
        for x in range(128,192):
            c=mul(p["armor"],1.12-0.25*(((y-64)%16)/15))
            if (y-64)%16==0 or x%16==0: c=mul(c,0.6)
            glow=((x%16==8) and (y%16 in (4,5,6,7,8,9,10,11))) or ((y%16==8) and (x%16 in (5,6,7,8,9,10,11)))
            if glow: c=mix(p["crystal"],p["light"],0.3)
            put(x,y,c,glow)
    # DARK
    for y in range(64,128):
        for x in range(192,256):
            c=mul(p["dark"],0.8+r.random()*0.3)
            put(x,y,c)
    im.save(f"{OUT}/grand_{el}.png"); gl.save(f"{OUT}/grand_{el}_glow.png")

for el in P: make(el)
print("ok")
