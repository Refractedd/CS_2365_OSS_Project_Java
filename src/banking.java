public class banking extends Thread
{
    private final Buffer buff;

    public banking(Buffer buff)
    {
        this.buff = buff;
    }

    public void run()
    {
        String msg;

        while(true)
        {
            msg = buff.receive();
            if(!msg.contains("new"))
                buff.sendReply("true,00000001");
            else
                buff.sendReply("true,00000001");
            System.out.println("[BANK] " + msg);
        }
    }
}
