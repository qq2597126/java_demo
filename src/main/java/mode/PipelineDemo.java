package mode;

/**
 * @author lcy
 * @DESC:
 * @date 2020/8/6.
 */
public class PipelineDemo {
    /**
     * 初始化头部不进行处理
     */
    public HandlerChainContext head = new HandlerChainContext(new AbstractHandler() {
        @Override
        public void doHandler(HandlerChainContext handlerChainContext, Object arg0) {
            handlerChainContext.runNext(arg0);
        }
    });

    public HandlerChainContext last =  head;

    public void requestProcess(Object arg0){
        this.head.handler(arg0);
    }
    public void addLast(AbstractHandler abstractHandler){
        if(last == head){
            last = new HandlerChainContext(abstractHandler);
            head.setNext(last);
        }else{
            HandlerChainContext lastContext = new HandlerChainContext(abstractHandler);
            last.setNext(lastContext);
            last = lastContext;
        }
    }

    public static void main(String[] args) {
        PipelineDemo pipelineDemo  = new PipelineDemo();
        pipelineDemo.addLast(new Handler1());
        pipelineDemo.addLast(new Handler2());
        pipelineDemo.addLast(new Handler2());
        pipelineDemo.addLast(new Handler1());
        pipelineDemo.requestProcess("火车头开启~~~");
    }
}

/**
 * 维护链和链的执行
 */
class HandlerChainContext{
    private HandlerChainContext next;
    private AbstractHandler handler;

    public HandlerChainContext(AbstractHandler handler) {
        this.handler = handler;
    }
    public void handler(Object arg0){
        handler.doHandler(this,arg0);
    }
    public void runNext(Object arg0){
        if(next != null){
            next.handler(arg0);
        }
    }

    public HandlerChainContext getNext() {
        return next;
    }

    public void setNext(HandlerChainContext next) {
        this.next = next;
    }
}

abstract class AbstractHandler{
    public abstract void doHandler(HandlerChainContext handlerChainContext,Object arg0);
}

// 处理器具体实现类
class Handler1 extends AbstractHandler {
    @Override
    public void doHandler(HandlerChainContext handlerChainContext, Object arg0) {
        arg0 = arg0.toString() + "..handler1的小尾巴.....";
        System.out.println("我是Handler1的实例，我在处理：" + arg0);
        // 继续执行下一个
        handlerChainContext.runNext(arg0);
    }
}

// 处理器具体实现类
class Handler2 extends AbstractHandler {
    @Override
    public void doHandler(HandlerChainContext handlerChainContext, Object arg0) {
        arg0 = arg0.toString() + "..handler2的小尾巴.....";
        System.out.println("我是Handler2的实例，我在处理：" + arg0);
        // 继续执行下一个
        handlerChainContext.runNext(arg0);
    }
}