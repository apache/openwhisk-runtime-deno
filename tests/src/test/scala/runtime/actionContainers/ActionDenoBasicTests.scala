/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package runtime.actionContainers

import actionContainers.ActionContainer.withContainer
import actionContainers.{ActionContainer, BasicActionRunnerTests}
import common.WskActorSystem
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ActionDenoBasicTests extends BasicActionRunnerTests with WskActorSystem {

  val image = "action-deno-v1.3.0"

  override def withActionContainer(env: Map[String, String] = Map.empty)(code: ActionContainer => Unit) = {
    withContainer(image, env)(code)
  }

  def withActionContainer(code: ActionContainer => Unit) =
    withContainer(image)(code)

  behavior of image

  override val testNoSourceOrExec = TestConfig("")

  override val testNotReturningJson = TestConfig(
    s"""
       |export default (args: any) => {
       |  return "this is not json";
       |}
    """.stripMargin,
    skipTest = true)

  override val testEcho = TestConfig(s"""
       |export default (args: any) => {
       |  console.error('hello stderr');
       |  console.log('hello stdout');
       |  return args;
       |}
    """.stripMargin)

  override val testUnicode = TestConfig(s"""
       |export default (args: any) => {
       |  const msg = args!.delimiter + " ☃ " + args!.delimiter;
       |  console.log(msg);
       |  return { "winter": msg };
       |}
    """.stripMargin)

  override val testEnv = TestConfig(s"""
       |export default (args: any) => {
       |  const env = Deno.env.toObject();
       |  return {
       |    "api_host": env['__OW_API_HOST'],
       |    "api_key": env['__OW_API_KEY'],
       |    "namespace": env['__OW_NAMESPACE'],
       |    "action_name": env['__OW_ACTION_NAME'],
       |    "action_version": env['__OW_ACTION_VERSION'],
       |    "activation_id": env['__OW_ACTIVATION_ID'],
       |    "deadline": env['__OW_DEADLINE']
       |  }
       |}
    """.stripMargin.trim)

  // the environment variables are ready at load time to ensure
  // variables are already available in the runtime
  override val testEnvParameters = TestConfig(s"""
       |const env = Deno.env.toObject();
       |const envargs = {
       |    "SOME_VAR": env.SOME_VAR,
       |    "ANOTHER_VAR": env.ANOTHER_VAR
       |}
       |
       |export default (args: any) => {
       |  return envargs
       |}
     """.stripMargin.trim)

  override val testInitCannotBeCalledMoreThanOnce = TestConfig(s"""
       |export default (args: any) => {
       |  return args;
       |}
    """.stripMargin)

  override val testEntryPointOtherThanMain = TestConfig(
    s"""
       |export default (args: any) => {
       |  return args;
       |}
    """.stripMargin,
    main = "niam")

  override val testLargeInput = TestConfig(s"""
       |export default (args: any) => {
       |  return args;
       |}
    """.stripMargin)
}
