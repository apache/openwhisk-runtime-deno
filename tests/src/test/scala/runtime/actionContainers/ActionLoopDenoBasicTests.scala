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
class ActionLoopPythonBasicTests extends BasicActionRunnerTests with WskActorSystem {

  val image = "ow-deno"

  override def withActionContainer(env: Map[String, String] = Map.empty)(code: ActionContainer => Unit) = {
    withContainer(image, env)(code)
  }

  def withActionLoopContainer(code: ActionContainer => Unit) =
    withContainer(image)(code)

  behavior of image

  override val testNoSourceOrExec = TestConfig("")

  override val testNotReturningJson =
    TestConfig("""
    |export default (args: unknown) => {
    |  return "this is not json"
    |};
    """.stripMargin)

  override val testEcho = TestConfig("""
    |export default (args: unknown) => {
    |  console.error('hello stderr');
    |  console.error('hello stdout');
    |  return args;
    |}
    """.stripMargin)

  override val testUnicode = TestConfig("""
       |export default (args: unknown) => {
       |  const msg = args.delimiter + " ☃ " + args.delimiter;
       |  console.log(msg)
       |  return { "winter": msg }
       |}
    """.stripMargin)

  override val testEnv = TestConfig("""
       |export default (args: unknown) => {
       |  return {
       |    "api_host":      Deno.env.get["__OW_API_HOST"],
       |    "api_key":       Deno.env.get["__OW_API_KEY"],
       |    "namespace":     Deno.env.get["__OW_NAMESPACE"],
       |    "activation_id": Deno.env.get["__OW_ACTIVATION_ID"],
       |    "action_name":   Deno.env.get["__OW_ACTION_NAME"],
       |    "deadline":      Deno.env.get["__OW_DEADLINE"]
       |  }
       |}
    """.stripMargin)

  override val testInitCannotBeCalledMoreThanOnce = TestConfig(s"""|export default (args: unknown) => {
       |  return args
       |}
    """.stripMargin)

  override val testEntryPointOtherThanMain = TestConfig(
    s"""|export default (args: unknown) => {
        |  return args
        |}
    """.stripMargin,
    main = "niam")

  override val testLargeInput = TestConfig(s"""
       |export default (args: unknown) => {
       |  return args
       |}
    """.stripMargin)
}
