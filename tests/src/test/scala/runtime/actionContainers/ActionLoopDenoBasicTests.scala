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
class ActionLoopDenoBasicTests extends BasicActionRunnerTests with WskActorSystem {

  val image = "actionloop-deno-v1.2.0"

  override def withActionContainer(env: Map[String, String] = Map.empty)(code: ActionContainer => Unit) = {
    withContainer(image, env)(code)
  }

  def withActionLoopContainer(code: ActionContainer => Unit) =
    withContainer(image)(code)

  behavior of image

  override val testNoSourceOrExec = TestConfig("")

  override val testNotReturningJson =
    TestConfig(
      """
    |export default (args: any) => {
    |  return "this is not json";
    |};
    """.stripMargin,
      skipTest = true)

  override val testEcho = TestConfig("""
    |export default (args: any) => {
    |  console.error('hello stderr');
    |  console.log('hello stdout');
    |  return args;
    |}
    """.stripMargin)

  override val testUnicode = TestConfig("""
       |export default (args: any) => {
       |  const msg = args!.delimiter + " ☃ " + args!.delimiter;
       |  console.log(msg);
       |  return { "winter": msg };
       |}
    """.stripMargin)

  override val testEnv = TestConfig("", skipTest = true)

  override val testInitCannotBeCalledMoreThanOnce = TestConfig(s"""|export default (args: any) => {
       |  return args;
       |}
    """.stripMargin)

  override val testEntryPointOtherThanMain = TestConfig(
    s"""|export default (args: any) => {
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
