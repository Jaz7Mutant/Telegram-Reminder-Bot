public interface UserIO {
    public void showMessage(String message, long chatId);

    public String getUserText(String prompt, long chatId); //TODO: May be null

    public int getOnClickButton(String[] buttons, long chatId);

    public void showList(String prompt, String[] elements, long chatId);

    //public String getChatId();
}
