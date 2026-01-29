//package kr.modusplant.infrastructure.aop.concurrency;
//
//import kr.modusplant.shared.concurrency.utils.VirtualThreadUtils;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//public class VirtualThreadAspect {
//
//    @Around("@annotation(kr.modusplant.shared.concurrency.annotation.RunOnVirtualThread) || " +
//            "@within(kr.modusplant.shared.concurrency.annotation.RunOnVirtualThread)")
//    public Object runOnVirtualThread(ProceedingJoinPoint joinPoint) {
//        return VirtualThreadUtils.submitAndGet(() -> {
//            try {
//                return joinPoint.proceed();
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//        });
//    }
//}
