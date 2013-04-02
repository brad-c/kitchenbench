package kitchenbench;

import java.lang.reflect.Method;

import net.minecraft.world.World;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class Crap {

  /**
   * @param args
   */
  public static void main(String[] args) {
    RealClass r = new RealClass(null);
    System.out.println("Non proxy output = " + r.realMethod1() + ", " + r.realMethod2());
    r = createProxy(r, null);
    System.out.println("Proxy output = " + r.realMethod1() + ", " + r.realMethod1(99) + ", " + r.realMethod2());

  }

  public static class RealClass {

    public int realMethod1() {
      return 1;
    }

    public int realMethod1(int foo) {
      return 1;
    }

    public int realMethod2() {
      return 1;
    }
    
    public RealClass(World o) {
      
    }

  }

  public static class MyInterceptor implements MethodInterceptor {

    private Object realObj;

    public MyInterceptor(Object obj) {

      this.realObj = obj;

    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
      System.out.println("Method name is: " + method.getName());
      Object res = method.invoke(realObj, objects);
      return res;
    }

  }

  public static <T> T createProxy(T obj, World arg) {
    Enhancer e = new Enhancer();
    e.setSuperclass(obj.getClass());
    e.setCallback(new MyInterceptor(obj));
    
    Class[] argTypes = new Class[] {World.class};
    Object[] args = new Object[] {arg};
    T proxifiedObj = (T) e.create(argTypes, args);
    return proxifiedObj;
  }

}
