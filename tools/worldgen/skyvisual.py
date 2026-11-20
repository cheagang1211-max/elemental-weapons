# 공중 세계 하늘 연출: 어두운 판타지 색감, 섬 아래 구름 바다, 수도의 빛 입자
import json, os
W = "src/main/resources/data/elementalweapons/worldgen/biome"
SKY = {  # (하늘, 지평선 안개)
    "capital_gardens": ("#3b4a94", "#5b5a9c"),
    "blossom_forest":  ("#7a4a86", "#8e6294"),
    "azure_forest":    ("#264f86", "#36608f"),
    "thunder_plateau": ("#262c72", "#363c74"),
    "gale_highlands":  ("#3f6a8c", "#5a7a98"),
    "volcanic_plains": ("#7a3226", "#7e4232"),
    "radiant_plains":  ("#86703a", "#94805a"),
    "shadow_woods":    ("#181028", "#261838"),
    "elemental_abyss": ("#10051a", "#1c0a28"),
    "elemental_ocean": ("#1c4280", "#285284"),
}
for name, (sky, fog) in SKY.items():
    p = f"{W}/{name}.json"
    b = json.load(open(p, encoding="utf-8"))
    a = b["attributes"]
    a["minecraft:visual/sky_color"] = sky
    a["minecraft:visual/fog_color"] = fog
    a["minecraft:visual/cloud_height"] = 112.0
    if name == "capital_gardens":
        a["minecraft:visual/ambient_particles"] = [{"particle": {"type": "minecraft:end_rod"}, "probability": 0.0012}]
    json.dump(b, open(p, "w", encoding="utf-8"), ensure_ascii=False, indent="\t")
print("ok")
# 수도: 벚나무를 크게 줄여 거리와 건물이 보이도록
P = "src/main/resources/data/elementalweapons/worldgen/placed_feature"
t = json.load(open(f"{P}/blossom_trees.json", encoding="utf-8"))
for m in t["placement"]:
    if m.get("type") == "minecraft:count": m["count"] = 1
json.dump(t, open(f"{P}/capital_trees.json", "w", encoding="utf-8"), ensure_ascii=False, indent="\t")
p = f"{W}/capital_gardens.json"; b = json.load(open(p, encoding="utf-8"))
b["features"][9] = []   # 수도 나무는 페인터가 손으로 심음
json.dump(b, open(p, "w", encoding="utf-8"), ensure_ascii=False, indent="\t")
print("capital trees ok")
