package com.tanhua.aop.aspect;

import com.tanhua.aop.util.LogRec;
import com.tanhua.aop.util.UserHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 切面处理类，操作日志异常日志记录处理
 */
@Aspect
@Component
public class LogRecAspect {


    /**
     * 设置操作日志切入点 记录操作日志 在注解的位置切入代码
     */
    @Pointcut("@annotation(com.tanhua.aop.util.LogRec)")
    public void logPoinCut() {
    }

    /**
     * @param joinPoint 切入点
     * @param result
     */
    @AfterReturning(value = "logPoinCut()", returning = "result")
    public void saveOperLog(JoinPoint joinPoint, Object result) {
        System.out.println("执行aop");

        //获取执行结果对象
        if (!(result instanceof String)) {
            return;
        }

        String busId = result.toString();
        System.out.println("获取的业务Id为:" + busId);


        try {
            // 通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            //获取注解
            LogRec loginRec = method.getAnnotation(LogRec.class);

            //获取注解信息
            if (loginRec != null) {
                System.out.println("注解信息,操作类型::" + loginRec.type() + "::" + loginRec.type().getCode());
                System.out.println("注解信息,路由key::" + loginRec.key());
                System.out.println("注解信息,操作说明::" + loginRec.note());
                System.out.println("注解信息,abc说明::" + loginRec.abc());
            }

            System.out.println("用户id：" + UserHolder.getUserId());
            System.out.println("执行保存日志方法");

        } catch (Exception e) {
            //保存日志记录不要影响业务逻辑
            e.printStackTrace();
        }
    }
}