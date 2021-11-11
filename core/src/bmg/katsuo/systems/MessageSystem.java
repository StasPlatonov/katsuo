package bmg.katsuo.systems;

import bmg.katsuo.objects.GameObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import bmg.katsuo.controllers.MessageController;
import bmg.katsuo.objects.GameObject.GameObjectChangeType;

/**
 * system to send messages between messageControllers within the same layer -
 * you may register other MessageSystems to this one in order to communicate
 * between layers
 */
public class MessageSystem extends LayerSystem
{
    /**
     * the message queue
     */
    private Array<GameMessage> QueuedMessages;
    /**
     * the interval at which to send out queued messages
     **/
    private float MessageInterval;
    /**
     * the amount of elapsed time currently passed since last message interval
     **/
    private float MessageElapsed;
    /**
     * the other systems linked to this one
     **/
    private Array<MessageSystem> LinkedSystems;
    /**
     * the game objects that have message controllers and their controllers
     **/
    private ArrayMap<GameObject, MessageController> MessageControllers;
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * the interval at which messages in the queue are served
     **/
    public float GetMessageInterval()
    {
        return MessageInterval;
    }

    /**
     * Set the interval at which messages in the queue are served
     * <p>
     * default is every two seconds
     **/
    public void SetMessageInterval(float message_interval)
    {
        this.MessageInterval = message_interval;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * link this message system to a system on a different layer
     * <p>
     * this will allow you to send messages between layers with relative ease
     **/
    public void LinkTo(MessageSystem messagesystem)
    {
        if (!LinkedSystems.contains(messagesystem, true))
        {
            LinkedSystems.add(messagesystem);
            messagesystem.LinkTo(messagesystem);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * get all the other message systems linked to this one
     **/
    protected Array<MessageSystem> GetLinkedSystems()
    {
        return LinkedSystems;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * adds a message the queue to send at a later time
     *
     * @param header - header of the message - used to filter
     * @param params - parameters of this message
     */
    public void AddMessageToQueue(String header, Object... params)
    {
        GameMessage g = new GameMessage(header, params);
        QueuedMessages.add(g);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * adds a message to the queue to send at a later time
     *
     * @param header - the header of this message
     */
    public void AddMessageToQueue(String header)
    {
        AddMessageToQueue(header);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * instantly sends a message to all message controllers
     *
     * @param header
     */
    public void InstantMessage(String header, Object... params)
    {
        InstantMessage(header, null, null, params);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * instantly sends a message to all message controllers of objects with the
     * given object name
     *
     * @param header
     * @param object_name - name of objects to message
     * @param params
     */
    public void InstantMessage(String header, String object_name, Object... params)
    {
        InstantMessage(header, object_name, null, params);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * instantly sends a message to the specified objects controller
     *
     * @param header
     * @param object - the object to send message to
     * @param params
     */
    public void InstantMessage(String header, GameObject object, Object... params)
    {
        InstantMessage(header, null, object, params);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    protected void InstantMessage(String header, String object_name, GameObject object, Object... params)
    {
        GameMessage m = new GameMessage(header, object_name, object, params);
        SendMessage(m);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public MessageSystem()
    {
        QueuedMessages = new Array<MessageSystem.GameMessage>();
        MessageControllers = new ArrayMap<GameObject, MessageController>();
        LinkedSystems = new Array<MessageSystem>();
        MessageElapsed = 0;
        MessageInterval = 2f; //default every two seconds
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Init(MapProperties properties)
    {
        MessageElapsed = 0;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnObjectAdded(GameObject object, GameObject parent)
    {
        MessageController mc = object.GetController(MessageController.class);
        if (mc != null)
        {
            MessageControllers.put(object, mc);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnObjectChanged(GameObject object, int type, float value)
    {
        if (type == GameObjectChangeType.CONTROLLER)
        {
            MessageController mc = object.GetController(MessageController.class);
            if (mc != null && !MessageControllers.containsKey(object))
            {
                MessageControllers.put(object, mc);
            }
            else if (mc == null && MessageControllers.containsKey(object))
            {
                MessageControllers.removeKey(object);
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnObjectRemoved(GameObject object, GameObject parent)
    {
        MessageControllers.removeKey(object);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Update(float delta, float timeScale)
    {
        MessageElapsed += delta;
        if (MessageElapsed > MessageInterval)
        {
            //send out mesasges
            while (QueuedMessages.size > 0)
            {
                GameMessage m = QueuedMessages.removeIndex(0);
                SendMessage(m);
            }

            //reset message_elapsed
            MessageElapsed = 0;
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * message sent out from this sytem to all message controllers in this area and
     * in other systems
     */
    protected void SendMessage(GameMessage m)
    {
        if (!RecieveMessage(m))
        {
            for (int i = 0; i < LinkedSystems.size; i++)
            {
                if (LinkedSystems.get(i).RecieveMessage(m))
                {
                    break;
                }
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * message recieved from other message systems return true if it was handled
     * (only relevant to object specific messages)
     **/
    protected boolean RecieveMessage(GameMessage m)
    {
        if (m.HasObject())
        {
            //message to specific object
            for (int i = 0; i < MessageControllers.size; i++)
            {
                GameObject o = MessageControllers.getKeyAt(i);
                if (o == m.GetObject())
                {
                    MessageController mc = MessageControllers.getValueAt(i);
                    mc.RecieveMessage(m.GetHeader(), m.GetParameters());
                    return true;
                }
            }
        }
        else if (m.HasObjectName())
        {
            //message to objects of specific name
            for (int i = 0; i < MessageControllers.size; i++)
            {
                GameObject o = MessageControllers.getKeyAt(i);
                if (o.getName().equals(m.GetObjectName()))
                {
                    MessageController mc = MessageControllers.getValueAt(i);
                    mc.RecieveMessage(m.GetHeader(), m.GetParameters());
                }
            }
        }
        else
        {
            for (int i = 0; i < MessageControllers.size; i++)
            {
                GameObject o = MessageControllers.getKeyAt(i);
                if (o.getName().equals(m.GetObjectName()))
                {
                    MessageController mc = MessageControllers.getValueAt(i);
                    mc.RecieveMessage(m.GetHeader(), m.GetParameters());
                }
            }
        }

        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void OnRemove()
    {
        QueuedMessages.clear();
        MessageElapsed = 0;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * game message object that contains a header and some generic parameter object
     *
     */
    public static class GameMessage
    {
        private String Header;

        private Object[] Params;

        private String ObjectName;

        private GameObject Object;
        //-------------------------------------------------------------------------------------------------------------------------

        private GameMessage(String header, String object_name, GameObject object, Object... params)
        {
            this.Header = header;
            this.Params = params;
            this.ObjectName = object_name;
            this.Object = object;
        }
        //-------------------------------------------------------------------------------------------------------------------------

        private GameMessage(String header, Object... params)
        {
            this(header, null, null, params);
        }

        private GameMessage(String header, GameObject object, Object... params)
        {
            this(header, null, object, params);
        }

        private GameMessage(String header, String object_name, Object... params)
        {
            this(header, object_name, null, params);
        }
        //-------------------------------------------------------------------------------------------------------------------------

        private String GetHeader()
        {
            return Header;
        }

        private Object[] GetParameters()
        {
            return Params;
        }

        private String GetObjectName()
        {
            return ObjectName;
        }

        private GameObject GetObject()
        {
            return Object;
        }
        //-------------------------------------------------------------------------------------------------------------------------

        private boolean HasObject()
        {
            return Object != null;
        }

        private boolean HasObjectName()
        {
            return ObjectName != null && !ObjectName.isEmpty();
        }

        private boolean HasParams()
        {
            return Params != null && Params.length > 0;
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
