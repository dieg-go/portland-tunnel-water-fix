# Portland Tunnel Water Fix

In GTA III the underwater tunnel between Portland and Staunton has a water block that clips up through the road surface. This CLEO script drops the water level below the tunnel floor while you're in that stretch, then puts it back.

<!-- Add a before/after screenshot to screenshots/tunnel.png, then uncomment:
![Before and after](screenshots/tunnel.png)
-->

**Platform:** GTA: III (PC) · **Type:** CLEO script · **Written:** 2017–18 · **Source:** included (`.sc`)

Written on request for another member of the modding community, so there's no release thread to link — if it was posted publicly at the time, it went up under their account rather than mine.

## The bug

The tunnel runs below sea level, and the water plane for that block doesn't know it's inside a tunnel — it renders straight through the road, so you get a sheet of water across the tarmac while driving through. Nothing in the map data stops it, because the tunnel geometry and the water plane are in different files and neither is aware of the other.

## The fix

The whole script is this:

```s
LVAR_INT scplayer
LVAR_FLOAT ms_aWaterZs

GET_PLAYER_CHAR (0) (scplayer)
WHILE IS_PC_VERSION
    WAIT (0)

    IF LOCATE_CHAR_ANY_MEANS_3D (scplayer, 769.0, 175.0, -18.0, 60.0, 20.0, 50.0, FALSE)
        READ_MEMORY (0x6654AC, 4, FALSE) (ms_aWaterZs)
        WRITE_MEMORY (0x6654AC, 4, -10.0, FALSE)

        WHILE LOCATE_CHAR_ANY_MEANS_3D (scplayer, 769.0, 175.0, -18.0, 60.0, 20.0, 50.0, FALSE)
            WAIT (0)
        ENDWHILE

        WRITE_MEMORY (0x6654AC, 4, ms_aWaterZs, FALSE)
    ENDIF
ENDWHILE
```

Walk it once per frame: if the player is inside a box around the tunnel, save the water Z value at `0x6654AC`, force it to `-10.0` — low enough to sit beneath the tunnel floor, so the surface stops intersecting the road — and then block until the player leaves. On exit, write the saved float back.

The "hide" is really a "lower": the water block is never removed, its height is just moved out of the way. That means it comes back correctly on its own when you drive out, and nothing about the water system's bookkeeping changes.

## The part worth keeping

The state handling, not the opcodes. Reading the original value before touching it and restoring it on exit is the difference between a hack that's invisible and a hack that leaves the water below the map for the rest of the session. The inner `WHILE` blocks the restore until the player has actually left the area, so the value can't be written back while they're still inside it.

That's the same discipline as the [Construction Site LOD Fix](https://github.com/dieg-go/construction-site-lod-fix): mutate the minimum, and put it back.

## Known issues

- **Reloading the script inside the zone poisons the saved value.** If the script is recompiled or reloaded while the player is standing in the tunnel, the value read on entry is already `-10.0`, and that's what gets restored. The workaround is to drive out before reloading. A guard (`if saved < -1 then skip restore`) would fix it.
- **The address is GTA III 1.0 only.** `0x6654AC` is hardcoded and there's no version check before the write.
- **`WHILE IS_PC_VERSION` is a strange infinite loop.** It always evaluates true, so it works, but `WHILE TRUE` is the idiomatic form and doesn't invite the question.
- **The trigger box is a flat hardcoded area** — 60 × 20 units around the tunnel mouth, with a z half-extent of 50, which covers essentially any altitude. Fine for a fixed location, useless if the map changes.

## Install

1. Install [CLEO 3](https://cleo.li/) for GTA III.
2. Copy `portland-tunnel-wl-fix.cs` into `GTA III/CLEO/`.

## Building from source

Open `portland-tunnel-wl-fix.sc` in [Sanny Builder](https://sannybuilder.com/) with the GTA III mode selected and compile to `.cs`.
