package de.mat2095.detunes;

public class InputProviderDummy implements InputProvider {

    public InputProviderDummy() {
    }

    @Override
    public boolean isButtonPressed(Button button) {
        return false;
    }
}
