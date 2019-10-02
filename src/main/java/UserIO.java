public interface UserIO {
    public void showMessage(String message);

    public String getUserText(String prompt); //TODO: May be null

    public int getOnClickButton(String[] buttons);

    public void showList(String prompt, String[] elements);

    public String getUserId();
}
