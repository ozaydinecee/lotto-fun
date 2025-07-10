package com.project.lottofun.factory;

import com.project.lottofun.model.entity.Draw;

public interface DrawFactory {
        Draw createInitialDraw();
        Draw createNextDraw(int previousDrawNumber);
}
