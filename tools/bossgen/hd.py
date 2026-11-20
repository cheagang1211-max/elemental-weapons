"""HD 변환: 모든 좌표·크기를 k배 (텍스처 밀도 k^2배). 발(y=24)은 그대로 땅에 닿게 유지."""
def hdify(roots, k=2):
    def scale_part(p, top):
        x, y, z = p.pivot
        p.pivot = (x * k, (y - 24) * k + 24, z * k) if top else (x * k, y * k, z * k)
        for b in p.boxes:
            b.x, b.y, b.z = b.x * k, b.y * k, b.z * k
            b.w, b.h, b.d = int(b.w * k), int(b.h * k), int(b.d * k)
        for c in p.children: scale_part(c, False)
    for r in roots: scale_part(r, True)
    return roots
