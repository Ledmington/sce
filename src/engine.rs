use crate::node::Node;

fn unbox<T>(value: &Box<T>) -> &T {
    &value
}

pub fn simplify(expr: &Node) -> &Node {
    match expr {
        Node::Bracket { expr: _inner_expr } => simplify_bracket(expr),
        _ => todo!(),
    }
}

pub fn simplify_bracket(expr: &Node) -> &Node {
    let n: Node::Bracket = expr;
}

#[cfg(test)]
mod tests {
    use super::*;
    use test_case::test_case;

    #[test_case(
        Node::Bracket{expr:Box::new(Node::Bracket{expr:Box::new(Node::Constant{value:3})})},
        Node::Bracket{expr:Box::new(Node::Constant{value:3})} ; "double_brackets")]
    #[test_case(
        Node::Bracket{expr:Box::new(Node::Constant{value:3})},
        Node::Constant{value:3} ; "brackets_on_constant")]
    #[test_case(
        Node::Bracket{expr:Box::new(Node::Variable { name: "x".to_string() })},
        Node::Variable { name: "x".to_string() } ; "brackets_on_variable")]
    fn generalization(input: Node, output: Node) {
        assert_eq!(simplify(&input), &output);
    }
}
