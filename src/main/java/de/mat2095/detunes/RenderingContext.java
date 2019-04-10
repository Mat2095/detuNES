package de.mat2095.detunes;

abstract class RenderingContext {

    abstract void setBufferData(int addr, int value);

    abstract void sync();

}
