package idgtl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by d.asadullin on 12.10.2016.
 */
@Aspect
public class Catch {

    @Around("execution(* org.telegram.telegrambots.api.objects.MessageEntity.computeText(..))")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable{
        try {
            Object output = pjp.proceed();
            return output;
        }catch (Exception ex) {
            return "";
        }
    }

    public static Catch aspectOf(){
       return new Catch();
    }

}
