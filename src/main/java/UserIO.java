public interface UserIO {
    public void showMessage(String message, String chatId);

    public String getUserText(String prompt, String chatId); //TODO: May be null

    public int getOnClickButton(String[] buttons, String chatId);

    public void showList(String prompt, String[] elements, String chatId);

    //public String getChatId();
}
