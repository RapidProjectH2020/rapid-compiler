group remoteableTemplates;

methods(modifiers, returnType, methodName, parameters, parameterTypes, parameterNames, originalCode) ::= <<
$modifiers$ $returnType$ $methodName$ ($parameters$) {
       Method toExecute;
       Class<?>[] paramTypes = {$parameterTypes$};
       Object[] paramValues = {$parameterNames$};
       $returnType$ result = null;
       try {
           toExecute = this.getClass().getDeclaredMethod("local$methodName$", paramTypes);
           result = ($returnType$) controller.execute(toExecute, paramValues, this);
       } catch (SecurityException e) {
           // Should never get here
           e.printStackTrace();
           throw e;
       } catch (NoSuchMethodException e) {
           // Should never get here
           e.printStackTrace();
       } catch (Throwable e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
       return result;
   }
   
>>