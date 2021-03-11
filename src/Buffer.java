//Define class
class Buffer
{
    private String Message;
    private String response;
    private boolean messageSet = false;
    private boolean replySet = false;

    public synchronized String receive()
    {
        while (!messageSet)
        {
            try
            {
                this.wait();
            }

            catch (InterruptedException e)
            {

                e.printStackTrace();
            }

        }

        messageSet = false;
        this.notify();

        return Message;

    }

    public synchronized void send(String Message)
    {
        while (messageSet)
        {
            try
            {
                this.wait();
            }

            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

        }

        this.Message = Message;
        this.messageSet = true;
        this.notify();
    }

    public synchronized String receiveReply()
    {
        while (!replySet)
        {
            try
            {
                this.wait();
            }

            catch (InterruptedException e)
            {

                e.printStackTrace();
            }

        }

        replySet = false;
        this.notify();

        return response;

    }

    public synchronized void sendReply(String resp)
    {
        while (replySet)
        {
            try
            {
                this.wait();
            }

            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

        }

        this.response = resp;
        this.replySet = true;
        this.notify();
    }
}
