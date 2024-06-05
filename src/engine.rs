use crate::node::Node;

pub fn generalize<'a>(expr: &'a Node) -> &'a Node<'a> {
    match expr {
        Node::Bracket {
            expr: Node::Constant { value: x },
        } => &Node::Constant { value: *x },
        Node::Bracket { expr } => &Node::Bracket {
            expr: &generalize(expr),
        },
        Node::Plus { lhs: _, rhs: _ } => todo!(),
        Node::Minus { lhs: _, rhs: _ } => todo!(),
        Node::Multiply { lhs: _, rhs: _ } => todo!(),
        Node::Divide { lhs: _, rhs: _ } => todo!(),
        Node::Power { lhs: _, rhs: _ } => todo!(),
        Node::Constant { value } => &Node::Constant { value: *value },
        Node::Variable { name } => &Node::Variable {
            name: name.to_owned(),
        },
        Node::Function { name, expr } => &Node::Function {
            name: name.to_owned(),
            expr: &generalize(&expr),
        },
    }
}

pub fn simplify<'a>(expr: &'a Node) -> &'a Node<'a> {
    match expr {
        Node::Bracket { expr } => todo!(),
        Node::Plus { lhs, rhs } => todo!(),
        Node::Minus { lhs, rhs } => todo!(),
        Node::Multiply { lhs, rhs } => todo!(),
        Node::Divide { lhs, rhs } => todo!(),
        Node::Power { lhs, rhs } => todo!(),
        Node::Constant { value } => todo!(),
        Node::Variable { name } => todo!(),
        Node::Function { name, expr } => todo!(),
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use test_case::test_case;

    #[test_case(
        Node::Bracket{expr:&Node::Bracket{expr:&Node::Constant{value:3}}},
        Node::Bracket{ expr: &Node::Constant{value:3} } ; "double_brackets")]
    #[test_case(
        Node::Bracket{expr:&Node::Constant{value:3}},
        Node::Constant{value:3} ; "brackets_on_constant")]
    #[test_case(
        Node::Bracket{expr:&Node::Variable { name: "x".to_string() }},
        Node::Variable { name: "x".to_string() } ; "brackets_on_variable")]
    fn generalization(input: Node, output: Node) {
        assert_eq!(*generalize(&input), output);
    }
}
