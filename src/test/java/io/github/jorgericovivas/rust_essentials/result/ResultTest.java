package io.github.jorgericovivas.rust_essentials.result;

import io.github.jorgericovivas.rust_essentials.option.Option;
import org.junit.jupiter.api.Assertions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

class ResultTest {
    
    @org.junit.jupiter.api.Test
    void joinErrors() {
        Result<Integer, Number> okSix = Result.ok(6);
        Result<Integer, Double> errorSixAndHalf = Result.err(6.5);
        Result<Integer, Integer> errorSix = Result.err(6);
        
        Option<Result<Integer, Number>> firstError = Result.joinErrors(
                okSix, errorSixAndHalf, errorSix
        );
        
        Assertions.assertEquals(firstError, Option.some(Result.err(6.5)));
    }
    
    @org.junit.jupiter.api.Test
    void firstError() {
        Result<Integer, Number> okSix = Result.ok(6);
        Result<Integer, Double> errorSixAndHalf = Result.err(6.5);
        Result<Integer, Integer> errorSix = Result.err(6);
        
        Option<Number> firstError = Result.firstError(okSix, errorSixAndHalf, errorSix);
        
        Assertions.assertEquals(firstError, Option.some(6.5));
    }
    
    @org.junit.jupiter.api.Test
    void joinOks() {
        Result<Double, Exception> errorException = Result.err(new Exception("Oh no, an error!"));
        Result<Double, Exception> okSixAndHalf = Result.ok(6.5);
        Result<Integer, Exception> okSix = Result.ok(6);
        
        Option<Result<Number, Exception>> firstOk = Result.joinOks(
                errorException, okSixAndHalf, okSix
        );
        
        Assertions.assertEquals(firstOk, Option.some(Result.ok(6.5)));
    }
    
    @org.junit.jupiter.api.Test
    void firstOk() {
        Result<Double, Exception> errorException = Result.err(new Exception("Oh no, an error!"));
        Result<Double, Exception> okSixAndHalf = Result.ok(6.5);
        Result<Integer, Exception> okSix = Result.ok(6);
        
        Option<Number> firstOk = Result.firstOk(
                errorException, okSixAndHalf, okSix
        );
        
        Assertions.assertEquals(firstOk, Option.some(6.5));
    }
    
    @org.junit.jupiter.api.Test
    void serialization() {
        Result<Integer, Exception> okSix = Result.ok(6);
        var serialized = Result.checked(() -> {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream);
            out.writeObject(okSix);
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        });
        var deserialized = serialized.map(serializedContents -> Result.checked(() -> {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder()
                                                                                       .decode(serializedContents));
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return (Ok<Integer, Exception>) objectInputStream.readObject();
        }));
        switch (deserialized) {
            case Err(var serializationError) ->
                    Assertions.fail("Deserialization is none, as serialized was wrong due to: " + serializationError);
            case Ok(Err(var deserializationError)) ->
                    Assertions.fail("Deserialization failed due to: " + deserializationError);
            case Ok(Ok(var res)) -> {
                Assertions.assertEquals(okSix, res);
                System.out.println("After serialize and deserialize the correct result is " + res);
            }
        }
    }
}