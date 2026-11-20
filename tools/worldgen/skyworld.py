"""공중 세계 데이터: 수도·심연 바이옴, 페인터 지물, 노이즈 설정, 세계 프리셋. 사용법: python3 tools/worldgen/skyworld.py <프로젝트 루트>"""
import json, os, sys, copy
R = sys.argv[1]; D = f"{R}/src/main/resources/data/elementalweapons"; W = f"{D}/worldgen"
def rd(p): return json.load(open(p, encoding="utf-8"))
def wr(p, o): os.makedirs(os.path.dirname(p), exist_ok=True); json.dump(o, open(p, "w", encoding="utf-8"), ensure_ascii=False, indent="\t")
# 바이옴
cap = rd(f"{W}/biome/blossom_forest.json")
cap["attributes"]["minecraft:gameplay/natural_mob_spawns"]["argument"]["spawns_by_category"] = {
    "creature": [{"type": "elementalweapons:fluffle", "count": {"type": "minecraft:uniform", "min_inclusive": 2, "max_inclusive": 4}, "weight": 10}]}
cap["attributes"]["minecraft:visual/sky_color"] = "#8fc4ff"
cap["features"][9] = ["elementalweapons:blossom_trees", "elementalweapons:radiant_trees"]
cap["features"][1] = []
wr(f"{W}/biome/capital_gardens.json", cap)
ab = rd(f"{W}/biome/shadow_woods.json")
sp = ab["attributes"]["minecraft:gameplay/natural_mob_spawns"]["argument"]["spawns_by_category"]
sp["monster"] = [{"type": f"elementalweapons:{m}", "count": {"type": "minecraft:uniform", "min_inclusive": 1, "max_inclusive": 3}, "weight": 20}
                 for m in ["shadow_stalker", "magma_brute", "spark_wisp", "light_sentinel", "gale_hawk", "tide_spirit", "thorn_beast"]]
ab["attributes"]["minecraft:visual/sky_color"] = "#2a0a3a"
ab["features"][9] = ["elementalweapons:shadow_trees", "elementalweapons:ember_trees"]
wr(f"{W}/biome/elemental_abyss.json", ab)
# 페인터 지물
wr(f"{W}/feature/sky_painter.json", {"type": "elementalweapons:sky_painter"})
wr(f"{W}/placed_feature/sky_painter.json", {"feature": "elementalweapons:sky_painter", "placement": []})
for f in os.listdir(f"{W}/biome"):
    b = rd(f"{W}/biome/{f}")
    b["features"][1] = [x for x in b["features"][1] if "lava" not in x]
    b["features"][9] = b["features"][9] * 1          # 나무 그대로
    if "elementalweapons:sky_painter" not in b["features"][10]: b["features"][10].append("elementalweapons:sky_painter")
    wr(f"{W}/biome/{f}", b)
# 나무 더 빽빽하게 (풍성하게)
for f in os.listdir(f"{W}/placed_feature"):
    if not f.endswith("_trees.json"): continue
    p = rd(f"{W}/placed_feature/{f}")
    for m in p["placement"]:
        if m.get("type") == "minecraft:count" and isinstance(m.get("count"), int): m["count"] = max(m["count"], 7)
    wr(f"{W}/placed_feature/{f}", p)
# 노이즈 설정
ns = rd(f"{W}/noise_settings/elemental.json")
sky = copy.deepcopy(ns)
sky["noise_router"]["final_density"] = {"type": "elementalweapons:sky_islands"}
sky["noise_router"]["chunk_surface_level"] = -64.0
sky["default_fluid"] = "minecraft:air"; sky["sea_level"] = -64
wr(f"{W}/noise_settings/sky.json", sky)
# 세계 프리셋: 원소 세계 = 공중 세계
regions = ["blossom_forest", "azure_forest", "thunder_plateau", "gale_highlands", "volcanic_plains", "radiant_plains", "shadow_woods"]
wr(f"{W}/world_preset/elemental_world.json", {"dimensions": {"minecraft:overworld": {"type": "minecraft:overworld", "generator": {
    "type": "minecraft:noise", "settings": "elementalweapons:sky",
    "biome_source": {"type": "elementalweapons:sky_regions", "capital": "elementalweapons:capital_gardens", "abyss": "elementalweapons:elemental_abyss",
                     "regions": [f"elementalweapons:{r}" for r in regions]}}}}})
# 흩어진 마을 제거
for p in [f"{W}/structure_set/towns.json", f"{W}/structure/town.json", f"{D}/tags/worldgen/biome/has_town.json", f"{D}/tags/worldgen/structure/towns.json"]:
    if os.path.exists(p): os.remove(p)
print("skyworld ok")
