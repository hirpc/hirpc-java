//package dev.hirpc.common.aspect;
//
//import dev.hirpc.common.domain.WebResponse;
//import dev.hirpc.common.domain.WebResponseBuilder;
//import dev.hirpc.common.exceptions.BasicException;
//import dev.hirpc.common.exceptions.ExceptionCode;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.validation.FieldError;
//import org.springframework.web.HttpMediaTypeNotSupportedException;
//import org.springframework.web.HttpRequestMethodNotSupportedException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.MissingServletRequestParameterException;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.servlet.NoHandlerFoundException;
//
//import javax.validation.ConstraintViolation;
//import javax.validation.ConstraintViolationException;
//import java.util.Set;
//
//@Slf4j
//@RestControllerAdvice
//public class ExceptionHandler {
//   @org.springframework.web.bind.annotation.ExceptionHandler(BasicException.class)
//   public WebResponse handlerBasicException(BasicException exception) {
//      log.warn(exception.getMessage());
//      return WebResponseBuilder.fail(exception.getCode(), exception.getMessage());
//   }
//
//   /**
//    * 404异常处理
//    */
//   @org.springframework.web.bind.annotation.ExceptionHandler(value = NoHandlerFoundException.class)
//   @ResponseStatus(HttpStatus.NOT_FOUND)
//   public WebResponse errorHandler(NoHandlerFoundException exception) {
//      Throwable throwable= new Throwable(exception);
//      throwable.printStackTrace();
//      return WebResponseBuilder.fail(400, throwable.getMessage());
//   }
//
//   /**
//    * 405异常处理
//    */
//   @org.springframework.web.bind.annotation.ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//   public WebResponse errorHandler(HttpRequestMethodNotSupportedException exception) {
////      exception.printStackTrace();
//      Throwable throwable= new Throwable(exception);
//      throwable.printStackTrace();
////      return WebResponseBuilder.fail(405, exception.getMessage());
//      return WebResponseBuilder.fail(405, throwable.getMessage());
//   }
//
//   /**
//    * 415异常处理
//    */
//   @org.springframework.web.bind.annotation.ExceptionHandler(HttpMediaTypeNotSupportedException.class)
//   public WebResponse errorHandler(HttpMediaTypeNotSupportedException exception) {
////      exception.printStackTrace();
////      return WebResponseBuilder.fail(415, exception.getMessage());
//      Throwable throwable= new Throwable(exception);
//      throwable.printStackTrace();
//      return WebResponseBuilder.fail(415, throwable.getMessage());
//   }
//
//   /**
//    * 参数缺失异常
//    * @param exception
//    * @return
//    */
//   @org.springframework.web.bind.annotation.ExceptionHandler(MissingServletRequestParameterException.class)
//   public WebResponse missingArgsException(MissingServletRequestParameterException exception) {
//      String message = String.format(
//            "未检测到参数[%s]的值, 参数类型为[%s]，该参数为必填项！",
//            exception.getParameterName(), exception.getParameterType()
//      );
//      log.warn(message);
//      return WebResponseBuilder.fail(ExceptionCode.ERROR, message);
//   }
//
//   /**
//    * 参数格式错误，例如定义传入JSON格式却未传入
//    * @param exception
//    * @return
//    */
//   @org.springframework.web.bind.annotation.ExceptionHandler(HttpMessageNotReadableException.class)
//   public WebResponse paramPatternException(HttpMessageNotReadableException exception){
//      String message = String.format(
//            "传入数据JSON格式不正确，异常信息[%s]", exception.getMessage()
//      );
//      log.warn(message);
//      return WebResponseBuilder.fail(ExceptionCode.ERROR, message);
//   }
//
//   /**
//    * 参数格式校验异常
//    * @param exception
//    * @return
//    */
//   @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
//   public WebResponse methodArgumentNotValidHandler(MethodArgumentNotValidException exception) {
//      FieldError fieldError = exception.getBindingResult().getFieldError();
//      String message = String.format("请求参数[%s]不符合要求, 参数值[%s]，错误信息：%s",
//            fieldError.getField(),
//            fieldError.getRejectedValue(),
//            fieldError.getDefaultMessage());
//      log.warn(message);
//      return WebResponseBuilder.fail(
//            ExceptionCode.ERROR,
//            message
//      );
//   }
//
////   /**
////    * 参数绑定异常处理
////    * @param exception
////    * @return
////    */
////   @org.springframework.web.bind.annotation.ExceptionHandler(BindException.class)
////   public WebResponse bindExceptionHandler(BindException exception) {
////      FieldError fieldError = exception.getFieldError();
////      String message = String.format(
////            "请求参数绑定异常（需要进一步修正异常信息），异常字段[%s], 参数值[%s],异常信息: %s",
////            fieldError.getField(),
////            fieldError.getRejectedValue(),
////            fieldError.getDefaultMessage());
////      return WebResponseBuilder.fail(
////            ExceptionCode.ERROR,
////            message
////      );
////   }
//
//   /**
//    * 参数校验
//    * @param exception
//    * @return
//    */
//   @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
//   public WebResponse validtorException(ConstraintViolationException exception) {
//      Set<ConstraintViolation<?>> constraintViolations =  exception.getConstraintViolations();
//      ConstraintViolation<?> violation = constraintViolations.iterator().next();
//      String message = violation.getMessage();
//      log.warn(message);
//      return  WebResponseBuilder.fail(ExceptionCode.ERROR, message);
//   }
//
//
//   /**
//    * 500异常处理
//    */
//   @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
//   public WebResponse errorHandler (Exception exception) {
//      exception.printStackTrace();
//      return  WebResponseBuilder.fail(ExceptionCode.ERROR, exception.getMessage());
//   }
//
//
//}
