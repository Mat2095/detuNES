package de.mat2095.detunes;

class InputProviderDummy implements InputProvider {

    InputProviderDummy() {
    }

    @Override
    public boolean isButtonPressed(Button button) {
        return false;
    }
}
