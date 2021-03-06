/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.operator.annotations;

import com.facebook.presto.common.type.TypeManager;
import com.facebook.presto.metadata.BoundVariables;
import com.facebook.presto.metadata.FunctionManager;
import com.facebook.presto.spi.function.FunctionHandle;
import com.facebook.presto.spi.function.InvocationConvention;

import java.lang.invoke.MethodHandle;
import java.util.Optional;

public abstract class ScalarImplementationDependency
        implements ImplementationDependency
{
    private final Optional<InvocationConvention> invocationConvention;

    protected ScalarImplementationDependency(Optional<InvocationConvention> invocationConvention)
    {
        this.invocationConvention = invocationConvention;
    }

    protected abstract FunctionHandle getFunctionHandle(BoundVariables boundVariables, FunctionManager functionManager);
    @Override
    public MethodHandle resolve(BoundVariables boundVariables, TypeManager typeManager, FunctionManager functionManager)
    {
        FunctionHandle functionHandle = getFunctionHandle(boundVariables, functionManager);
        if (invocationConvention.isPresent()) {
            return functionManager.getFunctionInvokerProvider().createFunctionInvoker(functionHandle, invocationConvention).methodHandle();
        }
        else {
            return functionManager.getBuiltInScalarFunctionImplementation(functionHandle).getMethodHandle();
        }
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
