public class BufferExample
{
    public static void main(String[] args)
    {
        Buffer buff = new Buffer();
        Producer pro = new Producer(buff);
        Consumer con = new Consumer(buff);

        pro.start();
        con.start();
    }
}

class Producer extends Thread
{
    private final Buffer buff;

    public Producer(Buffer buff)
    {
        this.buff = buff;
    }

    public void run()
    {
        while(true)
        {
            //Define array
            String[] Message = {"add(),1", "multiply(),1", "multiply(),2", "add(),2","add(),3","add(),4", "multiply(),3" };

            String resp;
            //Loop
            for(int i=0;i<7;i++)
            {
                //Send
                buff.send(Message[i]);
                System.out.println(buff.receiveReply());
            }

            //Exit
            System.exit(1);
        }
    }
}

class Consumer extends Thread
{
    private final Buffer buff;

    public Consumer(Buffer buff)
    {
        this.buff = buff;
    }

    public void run()
    {
        String msg;

        while(true)
        {
            msg = buff.receive();
            buff.sendReply("Replying to msg: '" + msg + "'");
            System.out.println(msg);
        }
    }
}



